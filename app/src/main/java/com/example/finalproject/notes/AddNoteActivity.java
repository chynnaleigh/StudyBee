package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.finalproject.courses.Course;
import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private EditText noteTitle, noteBody;
    private ImageButton backButton;
    private Button saveButton;

    private String noteId, courseId;
    private Note selectedNote;
    private Course course;

    private FirebaseFirestore db;
    private DocumentReference noteRef, courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.note_title_view);
        noteBody = findViewById(R.id.note_body_view);
        backButton = findViewById(R.id.new_note_back);
        saveButton = findViewById(R.id.new_note_save);

        db = FirebaseFirestore.getInstance();
        
        selectedNote = (Note) getIntent().getSerializableExtra("note");

        courseId = getIntent().getStringExtra("courseId");
        noteId = getIntent().getStringExtra("noteId");

        if(courseId != null) {
            Log.d("TAG", "AddNoteActivity -- courseId != null");
            courseRef = db.collection("courses").document(courseId);
            noteRef = courseRef.collection("notes").document();
            if (noteId != null) {
                Log.d("TAG", "AddNoteActivity -- notedId != null");
                noteTitle.setText(selectedNote.getTitle());
                noteBody.setText(selectedNote.getBody());
            }
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager.isAcceptingText()){
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }

                if(TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "Title is required to save",
                            Toast.LENGTH_SHORT).show();
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } else if (noteId != null) {
                    noteRef = courseRef.collection("notes").document(noteId);
                    Log.d("TAG", "AddNoteActivity -- noteRef " + noteRef);
                    if(title.equals(noteTitle) && body.equals(noteBody) ) {
                        Toast.makeText(getApplicationContext(), "No changes saved",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("title", title);
                        updates.put("body", body);

                        Log.d("TAG", "AddNoteActivity -- noteRef " + noteId);
                        noteRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void bVoid) {
                                Log.d("TAG", "AddNoteActivity -- NOTE UPDATED");
                                Toast.makeText(getApplicationContext(), "Updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddNoteActivity.this, "Could not save",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
                else {
                    noteRef = courseRef.collection("notes").document();
                    Log.d("TAG", "AddNoteActivity -- noteRef.getId() " + noteRef.getId());
                    Log.d("TAG", "AddNoteActivity -- noteRef.getId() " + courseRef.getId());

                    Note note = new Note();
                    note.setTitle(title);
                    note.setBody(body);
                    note.setNoteId(noteRef.getId());

                    saveNewNote(note);
                    Log.d("TAG", "NoteEditor" + noteRef);
                }

            }
        });
    }

    private void saveNewNote(Note note) {
        courseRef.collection("notes").add(note).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(AddNoteActivity.this, "Saved",
                        Toast.LENGTH_SHORT).show();
            }
        })
      .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNoteActivity.this, "Could not save",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}