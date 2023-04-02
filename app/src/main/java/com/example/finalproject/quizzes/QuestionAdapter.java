package com.example.finalproject.quizzes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<Question> questionList;
    private OnQuestionClickListener onQuestionClickListener;

    public QuestionAdapter(List<Question> questionList, OnQuestionClickListener onQuestionClickListener) {
        this.questionList = questionList;
        this.onQuestionClickListener = onQuestionClickListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_view_question, parent, false);
        return new QuestionViewHolder(itemView, onQuestionClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.questionItemView.setText(question.getQuestion());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView questionItemView;
        private OnQuestionClickListener onQuestionClickListener;

        public QuestionViewHolder(@NonNull View itemView, OnQuestionClickListener onQuestionClickListener) {
            super(itemView);
            questionItemView = itemView.findViewById(R.id.it_question_view);
            this.onQuestionClickListener = onQuestionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onQuestionClickListener.onQuestionItemClick(questionList.get(getAdapterPosition()));
        }
    }

    public interface OnQuestionClickListener {
        void onQuestionItemClick(Question question);
    }
}
