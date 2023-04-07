package com.example.finalproject.courses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.MainActivity;
import com.example.finalproject.flashcards.FlashcardActivity;
import com.example.finalproject.quizzes.QuizActivity;
import com.example.finalproject.R;
import com.example.finalproject.notes.NotesActivity;
import com.example.finalproject.quizzes.QuizCreatorActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CourseActivity extends AppCompatActivity {

    private TextView courseName, courseCode;
    private Button notesButton, flashcButton, quizButton;
    private ImageView backButton;

    private String courseId;
    private Course course, selectedCourse;
    private FirebaseFirestore db;
    private DocumentReference courseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        courseName = findViewById(R.id.course_title_view);
        courseCode = findViewById(R.id.course_code_view);
        backButton = findViewById(R.id.course_back_button);
        notesButton = findViewById(R.id.ac_notes_button);
        flashcButton = findViewById(R.id.ac_flashcards_button);
        quizButton = findViewById(R.id.ac_quizzes_button);


        courseId = getIntent().getStringExtra("courseId");

        db = FirebaseFirestore.getInstance();
        db.collection("courses").document(courseId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        if(task.isSuccessful()) {
                            if (document.exists()) {
                                // Get course details from Firestore
                                String getCourseName = document.getString("courseName");
                                String getCourseCode = document.getString("courseCode");

                                // Set the course name and course code in the views
                                courseName.setText(getCourseName);
                                courseCode.setText(getCourseName);
                            } else {
                                Toast.makeText(CourseActivity.this, "Error getting course info",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } else {
                            Toast.makeText(CourseActivity.this, "Error getting course info",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CourseActivity.this, MainActivity.class));
            }
        });

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseActivity.this, NotesActivity.class);
                intent.putExtra("course", course);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });

        flashcButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseActivity.this, FlashcardActivity.class);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });

        quizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseActivity.this, QuizActivity.class);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
    }
}