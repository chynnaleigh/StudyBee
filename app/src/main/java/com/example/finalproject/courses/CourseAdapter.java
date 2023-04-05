package com.example.finalproject.courses;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder>{
    private List<Course> courseList;
    private OnCourseClickListener onCourseClickListener, onCourseMenuClick;

    public CourseAdapter(List<Course> courseList, OnCourseClickListener onCourseClickListener,
                         OnCourseClickListener onCourseMenuClick) {
        this.courseList = courseList;
        this.onCourseClickListener = onCourseClickListener;
        this.onCourseMenuClick = onCourseMenuClick;
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
        return new CourseViewHolder(view, onCourseClickListener, onCourseMenuClick);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courseList.get(position);
        holder.courseItemName.setText(course.getCourseName());
        holder.courseItemCode.setText(course.getCourseCode());
        holder.courseItemEditButton.findViewById(R.id.it_menu_course_grid);
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView courseItemName;
        private TextView courseItemCode;
        private CardView courseGridLayout;
        private ImageView courseItemEditButton;
        private ConstraintLayout courseListLayout;
        private OnCourseClickListener onCourseClickListener,onCourseMenuClick;

        public CourseViewHolder(@NonNull View itemView, OnCourseClickListener onCourseClickListener,
                                OnCourseClickListener onCourseMenuClick) {
            super(itemView);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            int viewMode = preferences.getInt("view_mode", 0);
            if (viewMode == 0) {
                courseItemName = itemView.findViewById(R.id.it_title_course_grid);
                courseItemCode = itemView.findViewById(R.id.it_code_course_grid);
                courseItemEditButton = itemView.findViewById(R.id.it_menu_course_grid);
                courseGridLayout = itemView.findViewById(R.id.layout_course_grid);
            } else {
                courseItemName = itemView.findViewById(R.id.it_title_course_list);
                courseItemCode = itemView.findViewById(R.id.it_code_course_list);
                courseListLayout = itemView.findViewById(R.id.layout_course_list);
            }

            this.onCourseMenuClick = onCourseMenuClick;
            courseItemEditButton.setOnClickListener(this);

            this.onCourseClickListener = onCourseClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == courseItemEditButton) { // check if the edit menu button is clicked
                showPopupMenu(view); // call showPopupMenu() method
            } else {
                onCourseClickListener.onCourseItemClick(courseList.get(getAdapterPosition()));
            }
        }

        public void showPopupMenu(View v) {
            PopupMenu popMenu = new PopupMenu(itemView.getContext(), v);
            popMenu.inflate(R.menu.popup_menu);
            popMenu.setGravity(Gravity.END);

            popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_edit:
                            onCourseMenuClick.onCourseMenuEditClick(courseList.get(getAdapterPosition()));
                            return true;
                        case R.id.action_delete:
                            onCourseMenuClick.onCourseMenuDeleteClick(courseList.get(getAdapterPosition()));
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popMenu.show();
        }
    }

    public interface OnCourseClickListener {
        void onCourseItemClick(Course course);
        void onCourseMenuClick(Course course);
        void onCourseMenuEditClick(Course course);
        void onCourseMenuDeleteClick(Course course);
    }
}
