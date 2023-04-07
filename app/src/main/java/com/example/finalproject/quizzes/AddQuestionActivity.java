package com.example.finalproject.quizzes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddQuestionActivity extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener {
    private ImageView backButton;
    private Button addAnswerButton, saveAnswerButton;

    private TextView saveQuestionButton;
    private EditText questionTitle, answerEdit;
    private RecyclerView answerRecView;
    private Switch rightAnswerSwitch;

    private String courseId, quizId, questionId, answerId, quizTitle, prevQuestionTitle;
    private int answerCount, correctAnswerCount;
    private Date now = new Date();
    private long timestamp = now.getTime();
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
        saveQuestionButton = findViewById(R.id.question_save_button);
        addAnswerButton = findViewById(R.id.add_answer_button);
        questionTitle = findViewById(R.id.edit_title_question);

        answerRecView = findViewById(R.id.answer_recycler);
        answerRecView.setLayoutManager(new LinearLayoutManager(this));
        answerAdapter = new AnswerAdapter(answerList,this, 1);
        answerRecView.setAdapter(answerAdapter);

        prevQuestionTitle = getIntent().getStringExtra("questionTitle");
        if(!TextUtils.isEmpty(prevQuestionTitle)){
            questionTitle.setText(prevQuestionTitle);
        }

        courseId = getIntent().getStringExtra("courseId");
        quizId = getIntent().getStringExtra("quizId");
        quizTitle = getIntent().getStringExtra("quizTitle");
        questionId = getIntent().getStringExtra("questionId");
        answerCount = getIntent().getIntExtra("answerCount", 0);
        correctAnswerCount = getIntent().getIntExtra("correctAnswerCount", 0);

        Log.d("TAG", "AddQuestionActivity --- currentQuestionId " + questionId);

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        quizRef = courseRef.collection("quizzes").document(quizId);
        questionRef = quizRef.collection("questions").document(questionId);
        colAnswerRef = questionRef.collection("answers");
        answerRef = colAnswerRef.document();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionInput = questionTitle.getText().toString();
                boolean answersEmpty = true;

                // Check if any answer is not empty
                for (Answer answer : answerList) {
                    if (!TextUtils.isEmpty(answer.getAnswerOption()) || !TextUtils.isEmpty(prevQuestionTitle)) {
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
                intent.putExtra("quizTitle", quizTitle);
                startActivity(intent);
            }
        });

        addAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String questionInput = questionTitle.getText().toString();

                if(TextUtils.isEmpty(questionTitle.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter a question first",
                            Toast.LENGTH_SHORT).show();
                } else {
                    questionRef = quizRef.collection("questions").document(questionId);
                    questionRef.update("question", questionInput).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "AddQuestionActivity --- POPUP WINDOW");
                            createDialogBuilder(null);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddQuestionActivity.this, "Error creating new question",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        saveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = new Question(questionTitle.getText().toString(), questionId, timestamp, correctAnswerCount);

                if(TextUtils.isEmpty(questionTitle.getText())) {
                    Toast.makeText(getApplicationContext(), "Please enter a question",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!TextUtils.isEmpty(prevQuestionTitle)) {
                    questionRef = quizRef.collection("questions").document(questionId);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("question", question.getQuestion());
                    updates.put("correctAnswerCount", question.getCorrectAnswerCount());
                    questionRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Question updated",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddQuestionActivity.this, QuizCreatorActivity.class);
                            intent.putExtra("courseId", courseId);
                            intent.putExtra("quizId", quizId);
                            intent.putExtra("quizTitle", quizTitle);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddQuestionActivity.this, "Error creating new question",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    questionRef = quizRef.collection("questions").document(questionId);
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("question", question.getQuestion());
                    updates.put("correctAnswerCount", question.getCorrectAnswerCount());
                    questionRef.update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Question created",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(AddQuestionActivity.this, QuizCreatorActivity.class);
                            intent.putExtra("courseId", courseId);
                            intent.putExtra("quizId", quizId);
                            intent.putExtra("quizTitle", quizTitle);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddQuestionActivity.this, "Error creating new question",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        if(colAnswerRef != null) {
            colAnswerRef.orderBy("answerTimestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                // Not used for swipe gestures
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) {
                    // Delete the note with swipe the the left
                    Answer answerToDelete = answerList.get(position);
                    deleteAnswerFromFirebase(answerToDelete.getAnswerId());
                    answerList.remove(position);
                    answerAdapter.notifyItemRemoved(position);
                }
            }
        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(answerRecView);
    }

    private void deleteAnswerFromFirebase(String answerId) {
        colAnswerRef.document(answerId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        --correctAnswerCount;
                        questionRef.update("correctAnswerCount", correctAnswerCount);
                        Log.d("TAG", "Answer deleted successfully");
                        Toast.makeText(getApplicationContext(), "Answer deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error deleting answer", e);
                    }
                });
    }

    public void createDialogBuilder(Answer answer) {
        dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_new_answer, null);

        answerEdit = popView.findViewById(R.id.input_answer);
        saveAnswerButton = popView.findViewById(R.id.save_answer_button);
        rightAnswerSwitch = popView.findViewById(R.id.right_answer_switch);

        if (answer != null) {
            answerEdit.setText(answer.getAnswerOption());
            answerId = answer.getAnswerId();
            rightAnswerSwitch.setChecked(answer.getIsCorrect());
        }

        dialogBuilder.setView(popView);
        dialog = dialogBuilder.create();
        dialog.show();

        saveAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = new Question(questionTitle.getText().toString(), questionId, timestamp, correctAnswerCount);
                String answerInput = answerEdit.getText().toString();

                if(TextUtils.isEmpty(answerEdit.getText())) {
                    Toast.makeText(getApplicationContext(), "Please input answer",
                            Toast.LENGTH_SHORT).show();
                } else if (answer != null) {
                    answerRef = colAnswerRef.document(answer.getAnswerId());
                    answerRef.update("answerOption", answerInput);
                    answerRef.update("isCorrect", rightAnswerSwitch.isChecked());

                    if(rightAnswerSwitch.isChecked()) {
                        ++correctAnswerCount;
                        question.setCorrectAnswerCount(correctAnswerCount);
                        questionRef.update("correctAnswerCount", correctAnswerCount);
                    } else {
                        --correctAnswerCount;
                        question.setCorrectAnswerCount(correctAnswerCount);
                        questionRef.update("correctAnswerCount", correctAnswerCount);
                    }

                    Toast.makeText(getApplicationContext(), "Answer updated",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if(answerAdapter.getItemCount() < 5) {
                        answerRef = colAnswerRef.document();
                        Answer answer = new Answer();
                        answer.setAnswerOption(answerInput);
                        answer.setAnswerId(answerRef.getId());
                        answer.setIsCorrect(rightAnswerSwitch.isChecked());
                        answer.setAnswerTimestamp(timestamp);

                        if(rightAnswerSwitch.isChecked()) {
                            ++correctAnswerCount;
                            question.setCorrectAnswerCount(correctAnswerCount);
                            questionRef.update("correctAnswerCount", correctAnswerCount);
                        }

                        saveNewAnswer(answer);
                    } else {
                        Toast.makeText(getApplicationContext(), "Answer limit (5) reached",
                                Toast.LENGTH_SHORT).show();
                    }
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