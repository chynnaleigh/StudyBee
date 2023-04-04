package com.example.finalproject.quizzes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TakeQuizActivity extends AppCompatActivity implements AnswerAdapter.OnAnswerClickListener{
    private ImageButton backButton;
    private Button doneButton, nextQuestionButton, prevQuestionButton, finishButton, exitButton, cancelExitButton;
    private TextView quizTitleView, questionView, numberOfCorrectAnswersView, titleView, youView,
            userScore, outView, maxScore, exitQuizView;
    private RecyclerView answersView;

    private int currentQuestionIndex = 0;
    private int totalQuestions = 0;
    private int selectedAnswerCount = 0;
    private int numCorrectAnswers = 0;
    private int score = 0;
    private String courseId, quizId;
    private List<Answer> answerList = new ArrayList<>();
    private List<String> userAnswersId = new ArrayList<>();
    private List<String> rightAnswersId = new ArrayList<>();

    private AnswerAdapter answerAdapter;

    private FirebaseFirestore db;
    private DocumentReference courseRef, quizRef, questionRef;
    private CollectionReference answersRef, colQuestionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_quiz);

        Log.d("TAG", "TakeQuizActivity --- ONCREATE");

        backButton = findViewById(R.id.take_quiz_back_button);
        doneButton = findViewById(R.id.quiz_done_button);
        nextQuestionButton = findViewById(R.id.next_question_button);
        prevQuestionButton = findViewById(R.id.prev_question_button);
        quizTitleView = findViewById(R.id.take_quiz_title_view);
        questionView = findViewById(R.id.takeq_question_view);
        numberOfCorrectAnswersView = findViewById(R.id.numberOf_correct_answers_view);
        answersView = findViewById(R.id.takeq_answer_recycler);

        answersView.setLayoutManager(new LinearLayoutManager(this));
        answerAdapter = new AnswerAdapter(answerList, this, 2);
        answersView.setAdapter(answerAdapter);

        courseId = getIntent().getStringExtra("courseId");
        quizId = getIntent().getStringExtra("quizId");

        db = FirebaseFirestore.getInstance();
        courseRef = db.collection("courses").document(courseId);
        quizRef = courseRef.collection("quizzes").document(quizId);
        colQuestionRef = quizRef.collection("questions");
        questionRef = colQuestionRef.document();
        answersRef = questionRef.collection("answers");

        quizRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String quizTitle = documentSnapshot.getString("quizTitle");
                    totalQuestions = documentSnapshot.getLong("questionCount").intValue();
                    Log.d("TAG", "TakeQuizActivity --- NUMBER OF QUESTIONS IN THIS QUIZ " + totalQuestions);

                    if (quizTitle.length() > 20) {
                        quizTitle = quizTitle.substring(0, 20) + "...";
                    }

                    quizTitleView.setText(quizTitle);

                    loadQuestion(currentQuestionIndex);
                } else {
                    Log.d("TAG", "No such document");
                }
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuestionIndex++;
                if(currentQuestionIndex < totalQuestions) {
                    loadQuestion(currentQuestionIndex);
                } else {
                    checkScore();
                    createDialogBuilder();
                }
            }
        });

        prevQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentQuestionIndex--;
                if(currentQuestionIndex < 0) {
                    currentQuestionIndex = 0;
                }
                loadQuestion(currentQuestionIndex);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userAnswersId.size() != numCorrectAnswers) {
                    Toast.makeText(TakeQuizActivity.this,
                            "Please answer all questions before submitting", Toast.LENGTH_SHORT).show();
                } else {
                    checkScore();
                    createDialogBuilder();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createExitDialogBuilder();
            }
        });
    }

    private void createExitDialogBuilder() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_exit_quiz, null);

        exitQuizView = popView.findViewById(R.id.exit_quiz_dialog);
        cancelExitButton = popView.findViewById(R.id.cancel_exit_button);
        exitButton = popView.findViewById(R.id.exit_quiz_button);

        dialogBuilder.setView(popView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        cancelExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TakeQuizActivity.this, QuizActivity.class);
                intent.putExtra("courseId", courseId);
                startActivity(intent);
            }
        });
    }

    private void createDialogBuilder() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_quiz_score, null);

        titleView = popView.findViewById(R.id.score_title_view);
        youView = popView.findViewById(R.id.you_scored_view);
        userScore = popView.findViewById(R.id.user_score_view);
        outView = popView.findViewById(R.id.out_of_view);
        maxScore = popView.findViewById(R.id.max_score_view);
        finishButton = popView.findViewById(R.id.score_finish_button);

        userScore.setText(String.valueOf(score));
        maxScore.setText(String.valueOf(numCorrectAnswers));

        dialogBuilder.setView(popView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

    }

    private void checkScore() {
        for(String answer : userAnswersId) {
            if(rightAnswersId.contains(answer)) {
                score++;
            }
        }

        Log.d("TAG", "TakeQuizActivity --- Score: " + score);
        Log.d("TAG", "TakeQuizActivity --- Max. Score: " + numCorrectAnswers);
        double scorePercentage = (double) score/ (double) numCorrectAnswers * 100;
        Log.d("TAG", "TakeQuizActivity --- Score Percentage: " + scorePercentage);
    }

    private void loadQuestion(int currentQuestionIndex) {
        colQuestionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()) {
                    //Get question document at index, populate the question view
                    DocumentSnapshot questionSnapshot = queryDocumentSnapshots.getDocuments().get(currentQuestionIndex);
                    String question = questionSnapshot.getString("question");
                    questionView.setText(question);

                    //Get correctAnswerCount
                    Long correctAnswerCount = questionSnapshot.getLong("correctAnswerCount");
                    if(correctAnswerCount != null) {
                        numberOfCorrectAnswersView.setText(String.valueOf(correctAnswerCount));
                        numCorrectAnswers = numCorrectAnswers + correctAnswerCount.intValue();
                    } else {
                        Toast.makeText(TakeQuizActivity.this, "Error quiz is incomplete",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    answersRef = questionSnapshot.getReference().collection("answers");
                    answersRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            answerList.clear();
                            for (DocumentSnapshot answerSnapshot : queryDocumentSnapshots) {
                                String answerOption = answerSnapshot.getString("answerId");
                                boolean isCorrect = answerSnapshot.getBoolean("isCorrect");
                                Answer answer = answerSnapshot.toObject(Answer.class);
                                answerList.add(answer);
                                if(isCorrect == true) {
                                    rightAnswersId.add(answerOption);
                                    Log.d("TAG", "TakeQuizActivity --- Right Answers List: " + rightAnswersId);
                                }
                            }
                            Collections.shuffle(answerList);
                            answerAdapter.notifyDataSetChanged();

                            if(currentQuestionIndex == 0) {
                                prevQuestionButton.setVisibility(View.GONE);
                            } else {
                                prevQuestionButton.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onAnswerItemClick(Answer answer) {
        String selectedAnswerOptionId = answer.getAnswerId();

        //if answer has already been selected, remove from list
        if(userAnswersId.contains(selectedAnswerOptionId)) {
            userAnswersId.remove(selectedAnswerOptionId);
            selectedAnswerCount--;
        } else {
            userAnswersId.add(selectedAnswerOptionId);
            selectedAnswerCount++;
        }
        Log.d("TAG", "TakeQuizActivity --- Answers List: " + userAnswersId);
        Log.d("TAG", "TakeQuizActivity --- Answers Count: " + selectedAnswerCount);

//        for(Answer a : answerList) {
//            if(answers.contains(a.getAnswerOption())) {
//                a.setIsSelected(true);
//            } else {
//                a.setIsSelected(false);
//            }
//        }
        answerAdapter.notifyDataSetChanged();
    }
}
