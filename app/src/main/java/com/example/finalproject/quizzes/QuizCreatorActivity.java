package com.example.finalproject.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
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
import java.util.Collection;
import java.util.List;

public class QuizCreatorActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionClickListener{
    private EditText quizTitle;
    private RecyclerView questionRecView;
    private Button addQuestion, saveQuizButton;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private DocumentReference courseRef, quizRef, questionRef;
    private CollectionReference colQuestionRef;

    private String courseId, quizId;
    private List<Question> questionList = new ArrayList<>();
    private QuestionAdapter questionAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Log.d("TAG", "QuizCreaterActivity -- ONCREATE");

        quizTitle = findViewById(R.id.edit_quiz_title);
        addQuestion = findViewById(R.id.add_question_button);
        saveQuizButton = findViewById(R.id.new_quiz_save);
        backButton = findViewById(R.id.new_quiz_back);

        questionRecView = findViewById(R.id.quiz_creator_recycler);
        questionRecView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionList, this);
        questionRecView.setAdapter(questionAdapter);

        courseId = getIntent().getStringExtra("courseId");
        quizId = getIntent().getStringExtra("quizId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        quizRef = courseRef.collection("quizzes").document(quizId);
        colQuestionRef = quizRef.collection("questions");
        questionRef = colQuestionRef.document();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quizRef.collection("questions").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            quizRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "QuizCreatorActivity -- BLANK QUIZ DELETED");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "QuizCreatorActivity -- Unable to deleted blank quiz");
                                }
                            });
                        }
                        Intent intent = new Intent(QuizCreatorActivity.this, QuizActivity.class);
                        intent.putExtra("courseId", courseId);
                        startActivity(intent);
                    }
                });
            }
        });


        addQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = new Question();
                question.setQuestionId(questionRef.getId());

                questionRef.set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(QuizCreatorActivity.this, AddQuestionActivity.class);
                        intent.putExtra("courseId", courseId);
                        intent.putExtra("quizId", quizId);
                        intent.putExtra("questionId", question.getQuestionId());
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuizCreatorActivity.this, "Error creating new question",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        if(colQuestionRef != null) {
            Log.d("TAG", "QuizCreatorActivity --- QUESTION COLLECTION REF: " + colQuestionRef);
            colQuestionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    questionList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Question question = doc.toObject(Question.class);
                        questionList.add(question);
                    }

                    Log.d("TAG", "QuizCreatorActivity --- ONEVENT questionList.size() " + questionList.size());

                    questionAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onQuestionItemClick(Question question) {
        Intent intent = new Intent(QuizCreatorActivity.this, AddQuestionActivity.class);
        intent.putExtra("question", question);
        intent.putExtra("questionId", question.getQuestionId());
        intent.putExtra("courseId", courseId);
        intent.putExtra("quizId", quizId);
        startActivity(intent);
    }

}
