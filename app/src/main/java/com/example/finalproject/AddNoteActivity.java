package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddNoteActivity extends AppCompatActivity {

    private EditText noteTitle, noteBody;
    private ImageButton backButton;
    private Button saveButton;

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

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = noteTitle.getText().toString();
                String body = noteBody.getText().toString();

                if(TextUtils.isEmpty(title)) {
                    Toast.makeText(getApplicationContext(), "Title is required to save",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Note note = new Note(title, body);
                }

            }
        });
    }
}