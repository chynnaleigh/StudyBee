package com.example.finalproject.quizzes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.finalproject.MainActivity;
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

public class QuizActivity extends AppCompatActivity implements QuizAdapter.OnQuizClickListener {

    private FloatingActionButton addQuizButton;
    private RecyclerView quizListRecView;

    private FirebaseFirestore db;
    private DocumentReference courseRef, docQuizRef;
    private CollectionReference colQuizRef;

    private String courseId;
    private List<Quiz> quizList = new ArrayList<>();
    private QuizAdapter quizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Log.d("TAG", "QuizActivity -- ONCREATE");

        addQuizButton = findViewById(R.id.aq_add_quiz_button);
        quizListRecView = findViewById(R.id.quiz_recycler_view);

        quizListRecView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter(quizList, this);
        quizListRecView.setAdapter(quizAdapter);

        courseId = getIntent().getStringExtra("courseId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        colQuizRef = courseRef.collection("quizzes");
        docQuizRef = colQuizRef.document();


        addQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Quiz quiz = new Quiz();
                quiz.setQuizId(docQuizRef.getId());
                colQuizRef.document(docQuizRef.getId()).set(quiz).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "QuizActivity --- QUIZ CREATED");
                        Intent intent = new Intent(QuizActivity.this, QuizCreatorActivity.class);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("quizId", quiz.getQuizId());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizActivity.this, "Error creating new quiz",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        if (colQuizRef != null) {
            colQuizRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    quizList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Quiz quiz = doc.toObject(Quiz.class);
                        quizList.add(quiz);
                    }

                    quizAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onQuizItemClick(Quiz quiz) {
        Intent intent = new Intent(QuizActivity.this, QuizCreatorActivity.class);
        intent.putExtra("courseId", courseId);
        intent.putExtra("quizTitle", quiz.getQuizTitle());
        intent.putExtra("quizId", quiz.getQuizId());
        startActivity(intent);
    }
}