package com.example.finalproject.quizzes;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<Quiz> quizList;
    private OnQuizClickListener onQuizClickListener;

    public QuizAdapter(List<Quiz> quizList, OnQuizClickListener onQuizClickListener) {
        this.quizList = quizList;
        this.onQuizClickListener = onQuizClickListener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_quiz, parent, false);
        return new QuizViewHolder(itemView, onQuizClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        holder.titleItemQuiz.setText(quiz.getQuizTitle());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }


    public class QuizViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView titleItemQuiz;
        private OnQuizClickListener onQuizClickListener;

        public QuizViewHolder(@NonNull View itemView, OnQuizClickListener onQuizClickListener) {
            super(itemView);
            titleItemQuiz = itemView.findViewById(R.id.it_quiz_view);
            this.onQuizClickListener = onQuizClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onQuizClickListener.onQuizItemClick(quizList.get(getAdapterPosition()));
        }
    }

    public interface OnQuizClickListener{
        void onQuizItemClick(Quiz quiz);
    }
}
