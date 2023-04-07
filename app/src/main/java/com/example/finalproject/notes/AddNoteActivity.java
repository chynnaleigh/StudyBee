package com.example.finalproject.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNoteActivity extends AppCompatActivity {
    private EditText noteTitle, noteBody;
    private ImageView backButton;
    private TextView saveButton;

    private String noteId, courseId;
    private Note selectedNote;
    private Date now = new Date();
    private long timestamp = now.getTime();

    private FirebaseFirestore db;
    private DocumentReference docNoteRef, courseRef;
    private CollectionReference colNoteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.note_title_view);
        noteBody = findViewById(R.id.note_body_view);
        backButton = findViewById(R.id.new_note_back);
        saveButton = findViewById(R.id.new_note_save);
        
        selectedNote = (Note) getIntent().getSerializableExtra("note");

        courseId = getIntent().getStringExtra("courseId");
        noteId = getIntent().getStringExtra("noteId");

        if(courseId != null) {
            Log.d("TAG", "AddNoteActivity -- courseId != null");
            db = FirebaseFirestore.getInstance();
            courseRef = db.collection("courses").document(courseId);
            colNoteRef = courseRef.collection("notes");
            docNoteRef = colNoteRef.document();
            if (noteId != null) {
                Log.d("TAG", "AddNoteActivity -- notedId != null");
                noteTitle.setText(selectedNote.getTitle());
                noteBody.setText(selectedNote.getBody());
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddNoteActivity.this, NotesActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

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
                } else if (noteId != null) {
                    docNoteRef = courseRef.collection("notes").document(noteId);
                    Log.d("TAG", "AddNoteActivity -- noteRef " + docNoteRef);
                    if(title.equals(noteTitle) && body.equals(noteBody) ) {
                        Toast.makeText(getApplicationContext(), "No changes saved",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("title", title);
                        updates.put("body", body);

                        Log.d("TAG", "AddNoteActivity -- noteRef " + noteId);
                        docNoteRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
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
//                    colNoteRef = courseRef.collection("notes");
                    Log.d("TAG", "AddNoteActivity -- noteRef.getId() " + colNoteRef.getId());
                    Log.d("TAG", "AddNoteActivity -- noteRef.getId() " + courseRef.getId());

                    Note note = new Note();
                    note.setTitle(title);
                    note.setBody(body);
                    note.setNoteId(docNoteRef.getId());
                    note.setNoteTimestamp(timestamp);
                    Log.d("TAG", "AddNoteActivity --- docNoteRef.getId() " + docNoteRef.getId());

                    saveNewNote(note);
                    Log.d("TAG", "AddNoteActivity --- docNoteRef.getId() " + docNoteRef.getId());
                    Log.d("TAG", "NoteEditor" + colNoteRef);
                }

            }
        });
    }

    private void saveNewNote(Note note) {
        docNoteRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
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