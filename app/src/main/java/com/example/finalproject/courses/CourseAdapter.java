package com.example.finalproject.courses;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{
    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener;

    public CourseAdapter(List<Course> courseList, OnCourseClickListener onCourseClickListener) {
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(parent.getContext());
        int viewMode = pref.getInt("view_mode", 0);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_grid, parent, false);

        if(viewMode == 0) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_grid, parent, false);

        } else if(viewMode == 1) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_course_list, parent, false);
        }
        return new CourseViewHolder(view, onCourseClickListener);
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

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView courseItemName;
        private TextView courseItemCode;
        private LinearLayout courseGridLayout;
        private ConstraintLayout courseListLayout;
        private OnCourseClickListener onCourseClickListener;

        public CourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener) {
            super(itemView);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            int viewMode = preferences.getInt("view_mode", 0);
            if (viewMode == 0) {
                courseItemName = itemView.findViewById(R.id.it_title_course_grid);
                courseItemCode = itemView.findViewById(R.id.it_title_course_grid);
                courseGridLayout = itemView.findViewById(R.id.layout_course_grid);
            } else {
                courseItemName = itemView.findViewById(R.id.it_title_course_list);
                courseItemCode = itemView.findViewById(R.id.it_title_course_list);
                courseListLayout = itemView.findViewById(R.id.layout_course_grid);
            }

            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCourseClickListener.onCourseItemClick(courseList.get(getAdapterPosition()));
        }
    }

    public interface OnCourseClickListener {
        void onCourseItemClick(Course course);
    }
}
