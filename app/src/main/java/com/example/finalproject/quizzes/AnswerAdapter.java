package com.example.finalproject.quizzes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AnswersViewHolder> {
    private List<Answer> answerList;
    private OnAnswerClickListener onAnswerClickListener;

    public AnswerAdapter(List<Answer> answerList, OnAnswerClickListener onAnswerClickListener) {
        this.answerList = answerList;
        this.onAnswerClickListener = onAnswerClickListener;
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
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    public class AnswersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView answerItemView;
        private OnAnswerClickListener onAnswerClickListener;

        public AnswersViewHolder(@NonNull View itemView, OnAnswerClickListener onAnswerClickListener) {
            super(itemView);
            answerItemView = itemView.findViewById(R.id.it_answer_view);
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
