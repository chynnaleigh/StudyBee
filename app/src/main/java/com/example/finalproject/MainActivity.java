package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.courses.Course;
import com.example.finalproject.courses.CourseActivity;
import com.example.finalproject.courses.CourseAdapter;
import com.example.finalproject.notes.Note;
import com.example.finalproject.notes.NotesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener{

    private FloatingActionButton addCourseButton;
    private RecyclerView courseRecView;
    private RecyclerView.LayoutManager courseLayManager;
    private EditText inputCourseTitle;
    private EditText inputCourseCode;
    private TextView deleteCourseMessage;
    private Button saveButton, deleteButton, cancelDeleteButton;
    private ImageView popupMenuButton;


    private FirebaseFirestore db;
    private CollectionReference colCourseRef;
    private DocumentReference docCourseRef;

    private CourseAdapter courseAdapter;
    private List<Course> courseList = new ArrayList<>();

    private Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courseRecView = findViewById(R.id.course_rec_view);
        addCourseButton = findViewById(R.id.add_course_fButton);


        db = FirebaseFirestore.getInstance();
        colCourseRef = db.collection("courses");
        docCourseRef = colCourseRef.document(); // ref to a specific course

        updateViewSetting();

        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "MainActivity --- POPUP WIDOW OPEN");

                createDialogBuilder(null);
            }
        });

        settingsButton = findViewById(R.id.settings_button);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        if(colCourseRef != null) {
            colCourseRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    courseList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Course course = doc.toObject(Course.class);
                        courseList.add(course);
                    }

                    courseAdapter.notifyDataSetChanged();
                }
            });
        }
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
            // Grid view
            courseLayManager = new GridLayoutManager(this, 2);
            courseRecView.setLayoutManager(courseLayManager);
            courseAdapter = new CourseAdapter(courseList, this);
            courseRecView.setAdapter(courseAdapter);
        } else {
            // List view
            courseLayManager = new LinearLayoutManager(this);
            courseRecView.setLayoutManager(courseLayManager);
            courseAdapter = new CourseAdapter(courseList,this);
            courseRecView.setAdapter(courseAdapter);
        }
    }

    @Override
    public void onCourseItemClick(Course course) {
        Intent intent = new Intent(MainActivity.this, CourseActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("courseId", course.getCourseId());
        startActivity(intent);
    }

    @Override
    public void onCourseMenuEditClick(Course course) {
        createDialogBuilder(course);
    }

    @Override
    public void onCourseMenuDeleteClick(Course course) {
        createCourseDeleteBuilder(course);
    }


    public void createDialogBuilder(Course course) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_input_course, null);

        inputCourseTitle = popView.findViewById(R.id.input_title_course);
        inputCourseCode = popView.findViewById(R.id.input_code_course);
        saveButton = popView.findViewById(R.id.input_save_button);

        if(course != null) {
            inputCourseTitle.setText(course.getCourseName());
            inputCourseCode.setText(course.getCourseCode());
        }

        dialogBuilder.setView(popView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String courseTitle = inputCourseTitle.getText().toString();
                String courseCode = inputCourseCode.getText().toString();

                if(TextUtils.isEmpty(courseTitle)) {
                    Toast.makeText(getApplicationContext(), "Title is required to save",
                            Toast.LENGTH_SHORT).show();
                } else if (course != null) {
                    docCourseRef = colCourseRef.document(course.getCourseId());
                    docCourseRef.update("courseName", courseTitle);
                    docCourseRef.update("courseCode", courseCode);

                    Toast.makeText(getApplicationContext(), "Course information updated",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Course course = new Course();
                    course.setCourseName(courseTitle);
                    course.setCourseCode(courseCode);
                    course.setCourseId(docCourseRef.getId());

                    saveNewCourse(course);
                }

                dialog.dismiss();
            }
        });

    }

    public void saveNewCourse(Course course) {
        docCourseRef.set(course).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("TAG", "CourseCreate -- COURSE SAVED");
                Toast.makeText(getApplicationContext(), "Saved",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Could not save",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createCourseDeleteBuilder(Course course) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View popView = getLayoutInflater().inflate(R.layout.popup_delete_course, null);

        deleteCourseMessage = popView.findViewById(R.id.course_delete_message);
        deleteButton = popView.findViewById(R.id.course_delete_button);
        cancelDeleteButton = popView.findViewById(R.id.course_cancel_button);

        dialogBuilder.setView(popView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                docCourseRef = colCourseRef.document(course.getCourseId());

                docCourseRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Course has been deleted",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error deleting course",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.dismiss();
            }
        });

        cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                return;
            }
        });
    }
}