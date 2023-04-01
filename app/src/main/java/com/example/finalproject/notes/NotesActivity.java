package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.courses.Course;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.OnNoteClickListener {

    private FloatingActionButton addNoteButton;
    private RecyclerView notesListRecView;

    private NotesAdapter notesAdapter;

    private FirebaseFirestore db;
    private CollectionReference notesListRef;
    private DocumentReference courseRef;

    private List<Note> notesList = new ArrayList<>();
    private Course course;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        addNoteButton = findViewById(R.id.add_note_fButton);
        notesListRecView = findViewById(R.id.notes_recycler_view);
        notesListRecView.setLayoutManager(new LinearLayoutManager(this));
        notesAdapter = new NotesAdapter(notesList, this);
        notesListRecView.setAdapter(notesAdapter);

        db = FirebaseFirestore.getInstance();

        course = (Course) getIntent().getSerializableExtra("course");
        courseId = getIntent().getStringExtra("courseId");
        courseRef = db.collection("courses").document(courseId);
        notesListRef = courseRef.collection("notes");
        Log.d("TAG", "NotesActivity --- NOTE LIST REF: " + notesListRef);


        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        notesListRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Toast.makeText(NotesActivity.this, "Error retreiving notes",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                notesList.clear();

                for(DocumentSnapshot documentSnapshot : querySnapshot) {
                    Log.d("NotesActivity", "Retrieved note: " + documentSnapshot.getData());
                    Note note = documentSnapshot.toObject(Note.class);
//                    note.setNoteId(documentSnapshot.getId());
                    notesList.add(note);
                }

                notesAdapter.notifyDataSetChanged();
            }
        });

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                // Not used for swipe gestures
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    // Delete the note with swipe the the left
                    Note noteToDelete = notesList.get(position);
                    deleteNoteFromFirebase(noteToDelete.getNoteId());
                    notesList.remove(position);
                    notesAdapter.notifyItemRemoved(position);
                }
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(notesListRecView);
    }

    @Override
    public void onNoteItemClick(Note note) {
        Intent intent = new Intent(NotesActivity.this, AddNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("noteId", note.getNoteId());
        intent.putExtra("course", courseId);
        startActivity(intent);
    }

    private void deleteNoteFromFirebase(String noteId) {
        courseRef.collection("notes").document(noteId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "Note deleted successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting note", e);
                    }
                });
    }

}