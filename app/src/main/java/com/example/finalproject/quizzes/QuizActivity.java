package com.example.finalproject.quizzes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.finalproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuizActivity extends AppCompatActivity {

    private FloatingActionButton addQuizButton;
    private RecyclerView quizListRecView;

    private FirebaseFirestore db;

    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        addQuizButton = findViewById(R.id.aq_add_quiz_button);
        quizListRecView = findViewById(R.id.quiz_recycler_view);

        courseId = getIntent().getStringExtra("courseId");

        addQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuizActivity.this, AddQuizActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }
}