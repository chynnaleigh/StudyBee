package com.example.finalproject.quizzes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuizCreatorActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionClickListener{
    private EditText quizTitle;
    private RecyclerView questionRecView;
    private Button addQuestion, saveQuizButton;
    private ImageButton backButton;

    private FirebaseFirestore db;
    private DocumentReference courseRef, quizRef, questionRef;
    private CollectionReference colQuestionRef;

    private String courseId, quizId, prevQuizTitle;
    private Date now = new Date();
    private long timestamp = now.getTime();
    private int questionCount = 0;
    private List<Question> questionList = new ArrayList<>();
    private QuestionAdapter questionAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        Log.d("TAG", "QuizCreatorActivity -- ONCREATE");

        quizTitle = findViewById(R.id.edit_quiz_title);
        addQuestion = findViewById(R.id.add_question_button);
        saveQuizButton = findViewById(R.id.new_quiz_save);
        backButton = findViewById(R.id.new_quiz_back);

        questionRecView = findViewById(R.id.quiz_creator_recycler);
        questionRecView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionList, this);
        questionRecView.setAdapter(questionAdapter);

        prevQuizTitle = getIntent().getStringExtra("quizTitle");
        if(!TextUtils.isEmpty(prevQuizTitle)){
            Log.d("TAG", "QuizCreatorActivity -- QUIZ TITLE IS NOT EMPTY");
            quizTitle.setText(prevQuizTitle);
        }

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
                if(TextUtils.isEmpty(quizTitle.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter a quiz title first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String quizTitleInput = quizTitle.getText().toString();
                    quizRef.update("quizTitle", quizTitleInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Question question = new Question();
                            question.setQuestionId(questionRef.getId());
                            question.setQuestionTimestamp(timestamp);

                            colQuestionRef.document(questionRef.getId()).set(question).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    questionCount = questionAdapter.getItemCount();
                                    quizRef.update("questionCount", questionCount);
                                    Intent intent = new Intent(QuizCreatorActivity.this, AddQuestionActivity.class);
                                    intent.putExtra("courseId", courseId);
                                    intent.putExtra("quizId", quizId);
                                    intent.putExtra("quizTitle", quizTitleInput);
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
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuizCreatorActivity.this, "Error updating quiz",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        saveQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String quizTitleInput = quizTitle.getText().toString();

                if(TextUtils.isEmpty(quizTitle.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter a quiz title",
                            Toast.LENGTH_SHORT).show();
                } else if(!TextUtils.isEmpty(prevQuizTitle)) {
                    quizRef = courseRef.collection("quizzes").document(quizId);

                    quizRef.update("quizTitle", quizTitleInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Quiz updated",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(QuizCreatorActivity.this, QuizActivity.class);
                            intent.putExtra("courseId", courseId);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuizCreatorActivity.this, "Error updating quiz",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    quizRef = courseRef.collection("quizzes").document(quizId);
                    quizRef.update("quizTitle", quizTitleInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Quiz created",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(QuizCreatorActivity.this, QuizActivity.class);
                            intent.putExtra("courseId", courseId);
//                            intent.putExtra("quizTitle", quizTitleInput);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuizCreatorActivity.this, "Error creating new quiz",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if(colQuestionRef != null) {
            Log.d("TAG", "QuizCreatorActivity --- QUESTION COLLECTION REF: " + colQuestionRef);
            colQuestionRef.orderBy("questionTimestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        String quizTitleInput = quizTitle.getText().toString();
        Intent intent = new Intent(QuizCreatorActivity.this, AddQuestionActivity.class);
        intent.putExtra("questionTitle", question.getQuestion());
//        intent.putExtra("answerCount", question.getAnswerCount());
        intent.putExtra("questionId", question.getQuestionId());
        intent.putExtra("courseId", courseId);
        intent.putExtra("quizId", quizId);
        intent.putExtra("quizTitle", quizTitleInput);
        startActivity(intent);
    }

}