package com.example.finalproject.quizzes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.finalproject.R;
import com.example.finalproject.courses.Course;
import com.example.finalproject.notes.Note;
import com.example.finalproject.notes.NotesActivity;
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
import java.util.List;

public class AddQuestionActivity extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener {
    private ImageButton backButton;
    private Button saveQuestionButton, addAnswerButton, saveAnswerButton;
    private EditText questionTitle, answerEdit;
    private RecyclerView answerRecView;

    private String courseId, quizId, questionId, answerId;
    private Question selectedQuestion;
    private List<Answer> answerList = new ArrayList<>();
    private AnswerAdapter answerAdapter;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private FirebaseFirestore db;
    private DocumentReference courseRef, quizRef, questionRef, answerRef;
    private CollectionReference colAnswerRef, colQuestionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        Log.d("TAG", "AddQuestionActivity -- ONCREATE");

        backButton = findViewById(R.id.new_quiz_back_button);
        saveQuestionButton = findViewById(R.id.quiz_save_button);
        addAnswerButton = findViewById(R.id.add_answer_button);
        questionTitle = findViewById(R.id.edit_title_question);

        answerRecView = findViewById(R.id.answer_recycler);
        answerRecView.setLayoutManager(new LinearLayoutManager(this));
        answerAdapter = new AnswerAdapter(answerList, this);
        answerRecView.setAdapter(answerAdapter);

        selectedQuestion = (Question) getIntent().getSerializableExtra("question");

        courseId = getIntent().getStringExtra("courseId");
        quizId = getIntent().getStringExtra("quizId");
        questionId = getIntent().getStringExtra("questionId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        quizRef = courseRef.collection("quizzes").document(quizId);
        questionRef = quizRef.collection("questions").document(questionId);
        colAnswerRef = quizRef.collection("answers");
        answerRef = colAnswerRef.document();

//        if(questionId != null) {
//            if(questionTitle != null) {
//                questionTitle.setText(selectedQuestion.getQuestion());
//            }
//        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionInput = questionTitle.getText().toString();
                boolean answersEmpty = true;

                // Check if any answer is not empty
                for (Answer answer : answerList) {
                    if (!TextUtils.isEmpty(answer.getAnswerOption())) {
                        answersEmpty = false;
                        break;
                    }
                }

                // Delete the question if both question and answers are empty
                if (TextUtils.isEmpty(questionInput) && answersEmpty) {
                    questionRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "Question deleted because both question and answers were empty");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Failed to delete question");
                        }
                    });
                }

                Intent intent = new Intent(AddQuestionActivity.this, QuizCreatorActivity.class);
                intent.putExtra("courseId", courseId);
                intent.putExtra("quizId", quizId);
                startActivity(intent);
            }
        });


//        questionTitle.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                questionTitlePopulate(question);
//                Question question = new Question();
//                String questionInput = questionTitle.getText().toString();
//                question.setQuestion(questionInput);
//                question.setQuestionId(questionRef.getId());
//                questionRef.update("question", question);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });

        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "AddQuestionActivity --- POPUP WINDOW");
                createDialogBuilder(null);
            }
        });

        saveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionInput = questionTitle.getText().toString();

                if(questionTitle == null) {
                    Toast.makeText(getApplicationContext(), "Please enter a question",
                            Toast.LENGTH_SHORT).show();
                } else if (questionTitle != null) {
                    questionRef = quizRef.collection("questions").document(questionId);
                    questionRef.update("questionInput", questionInput);
                    Toast.makeText(getApplicationContext(), "Question updated",
                            Toast.LENGTH_SHORT).show();

                    finish();
                } else {
                    Question question = new Question();
                    question.setQuestion(questionInput);
                    question.setQuestionId(questionRef.getId());
                    questionRef.update("question", question);

                    finish();
                }
            }
        });

        if(colAnswerRef != null) {
            colAnswerRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    answerList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Answer answer = doc.toObject(Answer.class);
                        answerList.add(answer);
                    }

                    answerAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    public void questionTitlePopulate(Question question) {
        if(question != null) {
            questionTitle.setText(question.getQuestion());
            questionRef = quizRef.collection("questions").document(questionId);
            String questionInput = questionTitle.getText().toString();
            questionRef.update("questionInput", questionInput);
        }
        return;
    }

    public void createDialogBuilder(Answer answer) {
        dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_new_answer, null);

        answerEdit = popView.findViewById(R.id.input_answer);
        saveAnswerButton = popView.findViewById(R.id.save_answer_button);

        if (answer != null) {
            answerEdit.setText(answer.getAnswerOption());
        }

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        saveAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answerInput = answerEdit.getText().toString();

                if(TextUtils.isEmpty(answerInput)) {
                    Toast.makeText(getApplicationContext(), "Please input answer",
                            Toast.LENGTH_SHORT).show();
                }
                else if (answer != null) {
                    answerRef = colAnswerRef.document(answerRef.getId());
                    answerRef.update("answerInput", answerInput);

                    Toast.makeText(getApplicationContext(), "Answer updated",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Answer answer = new Answer();
                    answer.setAnswerOption(answerInput);
                    answerId = answer.getAnswerId();

                    saveNewAnswer(answer);
                }
                dialog.dismiss();
            }
        });

    }

    public void saveNewAnswer(Answer answer) {
        answerRef.set(answer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "AnswerOptionCreate -- ANSWER OPTION SAVED");
                Toast.makeText(getApplicationContext(), "Added",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddQuestionActivity.this, "Could not add",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAnswerItemClick(Answer answer) {
        createDialogBuilder(answer);
    }
}