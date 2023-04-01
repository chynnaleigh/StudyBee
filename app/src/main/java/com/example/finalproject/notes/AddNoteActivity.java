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
    private String noteId;
    Note selectedNote;

    private FirebaseFirestore db;
    private DocumentReference noteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        noteTitle = findViewById(R.id.note_title_view);
        noteBody = findViewById(R.id.note_body_view);
        backButton = findViewById(R.id.new_note_back);
        saveButton = findViewById(R.id.new_note_save);

        db = FirebaseFirestore.getInstance();
        noteRef = db.collection("notes").document();
        
        selectedNote = (Note) getIntent().getSerializableExtra("note");

        noteId = getIntent().getStringExtra("noteId");

        if(noteId != null) {
            noteTitle.setText(selectedNote.getTitle());
            noteBody.setText(selectedNote.getBody());
            noteRef = db.collection("notes").document(noteId);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();

                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                if(TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "Title is required to save",
                            Toast.LENGTH_SHORT).show();
                } else if (noteId != null) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("title", title);
                    updates.put("body", body);
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
                else {
                    Note note = new Note();
                    note.setTitle(title);
                    note.setBody(body);
                    note.setNoteId(noteRef.getId());

                    saveNewNote(note);
                }

            }
        });
    }

    private void saveNewNote(Note note) {
        noteRef.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void bVoid) {
                Log.d("TAG", "AddNoteActivity -- NOTE SAVED");
                Toast.makeText(getApplicationContext(), "Saved",
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