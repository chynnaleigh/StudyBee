package com.example.finalproject.flashcards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.courses.Course;
import com.example.finalproject.courses.CourseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FlashcardActivity extends AppCompatActivity implements FlashcardSetAdapter.OnFlashcardSetClickListener {
    private ImageView addFlashcardButton, backButton;
    private RecyclerView flashcardRecycler;

    private String courseId;
    private List<Flashcard> flashcardList = new ArrayList<>();
    private Date now = new Date();
    private long timestamp = now.getTime();
    private FlashcardSetAdapter flashcardSetAdapter;
    private Course course;

    private FirebaseFirestore db;
    private CollectionReference colFlashcardsRef;
    private DocumentReference docFlashcardsRef, courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard);

        addFlashcardButton = findViewById(R.id.add_flashcards_button);
        backButton = findViewById(R.id.back_button_fActivity);
        flashcardRecycler = findViewById(R.id.flashcard_recycler);

        flashcardRecycler.setLayoutManager(new LinearLayoutManager(this));
        flashcardSetAdapter = new FlashcardSetAdapter(flashcardList, this);
        flashcardRecycler.setAdapter(flashcardSetAdapter);

        course = (Course) getIntent().getSerializableExtra("course");
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
                        Intent intent = new Intent(FlashcardActivity.this, AddFlashcardsActivity.class);
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FlashcardActivity.this, CourseActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });

        if(colFlashcardsRef != null) {
            colFlashcardsRef.orderBy("flashcardSetTimestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                    if(error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    flashcardList.clear();

                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Flashcard flashcard = documentSnapshot.toObject(Flashcard.class);
                        flashcardList.add(flashcard);
                    }

                    flashcardSetAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onFlashcardSetItemClick(Flashcard flashcard) {
        Intent intent = new Intent(FlashcardActivity.this, DoFlashcardsActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("flashcardSetTitle", flashcard.getFlashcardSetTitle());
        intent.putExtra("flashcardSetId", flashcard.getFlashcardSetId());
        startActivity(intent);
    }
}