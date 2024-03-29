package com.example.finalproject.quizzes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswersViewHolder> {
    private List<Answer> answerList;
    private OnAnswerClickListener onAnswerClickListener;
    private int activity;

    public AnswerAdapter(List<Answer> answerList, OnAnswerClickListener onAnswerClickListener, int activity) {
        this.answerList = answerList;
        this.onAnswerClickListener = onAnswerClickListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public AnswersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_view_answer, parent, false);
        return new AnswersViewHolder(itemView, onAnswerClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswersViewHolder holder, int position) {
        Answer answer = answerList.get(position);
        holder.answerItemView.setText(answer.getAnswerOption());
        if(activity == 1) {
            if (answer.getIsCorrect()) {
                holder.correctAnswer.setVisibility(View.VISIBLE);
            } else {
                holder.correctAnswer.setVisibility(View.GONE);
            }
        } else {
            holder.correctAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView answerItemView;
        public ImageView correctAnswer;
        private OnAnswerClickListener onAnswerClickListener;

        public AnswersViewHolder(@NonNull View itemView, OnAnswerClickListener onAnswerClickListener) {
            super(itemView);
            answerItemView = itemView.findViewById(R.id.it_answer_view);
            correctAnswer = itemView.findViewById(R.id.correct_answer_image);
            this.onAnswerClickListener = onAnswerClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onAnswerClickListener.onAnswerItemClick(answerList.get(getAdapterPosition()));
        }
    }

    public interface OnAnswerClickListener {
        void onAnswerItemClick(Answer answer);
    }
}
