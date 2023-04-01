package com.example.finalproject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{
    private List<Course> courseList;

    public CourseAdapter(List<Course> courseList) {
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        int viewMode = pref.getInt("view_mode", 0);
        View view;

        if(viewMode == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_list, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_grid, parent, false);
        }

        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseItemName.setText(course.getCourseName());
        holder.courseItemCode.setText(course.getCourseCode());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        private TextView courseItemName;
        private TextView courseItemCode;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            int viewMode = preferences.getInt("view_mode", 0);

            if (viewMode == 0) {
                courseItemName = itemView.findViewById(R.id.it_title_course_list);
                courseItemCode = itemView.findViewById(R.id.it_title_course_list);
            } else {
                courseItemName = itemView.findViewById(R.id.it_title_course_grid);
                courseItemCode = itemView.findViewById(R.id.it_title_course_list);
            }

        }
    }
}
