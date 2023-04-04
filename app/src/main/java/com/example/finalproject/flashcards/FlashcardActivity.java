package com.example.finalproject.flashcards;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.quizzes.QuizActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class FlashcardActivity extends AppCompatActivity {
    private FloatingActionButton addFlashcardButton;
    private RecyclerView flashcardRecycler;

    private String courseId;
    private Date now = new Date();
    private long timestamp = now.getTime();

    private FirebaseFirestore db;
    private CollectionReference colFlashcardsRef;
    private DocumentReference docFlashcardsRef, courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        addFlashcardButton = findViewById(R.id.add_flashcard_button);
        flashcardRecycler = findViewById(R.id.flashcard_recycler);

        courseId = getIntent().getStringExtra("courseId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        colFlashcardsRef = courseRef.collection("flashcards");
        docFlashcardsRef = courseRef.collection("flashcards").document();

        addFlashcardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flashcard flashcard = new Flashcard();
                flashcard.setFlashcardSetId(docFlashcardsRef.getId());
                flashcard.setFlashcardSetTimestamp(timestamp);

                colFlashcardsRef.document(docFlashcardsRef.getId()).set(flashcard)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG","FlashcardActiviy --- FLASHCARD SET CREATED");
                        Intent intent = new Intent(FlashcardActivity.this, AddFlashcardSetActivity.class);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("flashcardSetId", flashcard.getFlashcardSetId());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FlashcardActivity.this, "Error creating new flashcards",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}