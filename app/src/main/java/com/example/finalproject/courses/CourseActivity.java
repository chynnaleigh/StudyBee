package com.example.finalproject.courses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.notes.NotesActivity;

public class CourseActivity extends AppCompatActivity {

    private TextView courseName, courseCode;
    private Button notesButton, flashcButton, quizButton;

    private String courseId;
    private Course course, selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        courseName = findViewById(R.id.course_title_view);
        courseCode = findViewById(R.id.course_code_view);
        notesButton = findViewById(R.id.ac_notes_button);
        flashcButton = findViewById(R.id.ac_flashcards_button);
        quizButton = findViewById(R.id.ac_quizzes_button);

        selectedCourse = (Course) getIntent().getSerializableExtra("course");
        courseName.setText(selectedCourse.getCourseName());
        courseCode.setText(selectedCourse.getCourseCode());
        courseId = getIntent().getStringExtra("courseId");

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseActivity.this, NotesActivity.class);
                intent.putExtra("courseId",courseId);
                startActivity(intent);
            }
        });
    }
}