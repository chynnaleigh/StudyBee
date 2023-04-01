package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.finalproject.notes.NotesActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton addCourseButton;
    private RecyclerView courseRecView;
    private RecyclerView.LayoutManager courseLayManager;

    private CourseAdapter courseAdapter;

    private FirebaseFirestore db;
    private CollectionReference courseRef;

    private List<Course> courseList = new ArrayList<>();

    private Button notesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courseRecView = findViewById(R.id.course_rec_view);
        addCourseButton = findViewById(R.id.add_course_fButton);

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InputCourseActivity.class));
            }
        });

        notesButton = findViewById(R.id.notes_btn);

        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NotesActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViewSetting();
    }

    private void updateViewSetting() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int viewMode = pref.getInt("view_mode", 0);
        if (viewMode == 0) {
            // List view
            courseRecView = findViewById(R.id.course_rec_view);
            courseLayManager = new LinearLayoutManager(this);
            courseAdapter = new CourseAdapter(courseList);
            courseRecView.setLayoutManager(courseLayManager);
            courseRecView.setAdapter(courseAdapter);
        } else {
            // Grid view
            courseRecView = findViewById(R.id.course_rec_view);
            courseLayManager = new GridLayoutManager(this, 2);
            courseAdapter = new CourseAdapter(courseList);
            courseRecView.setLayoutManager(courseLayManager);
            courseRecView.setAdapter(courseAdapter);
        }
    }
}