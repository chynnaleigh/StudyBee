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

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<Question> questionList;
    private OnQuestionClickListener OnQuestionClickListener;

    public QuestionAdapter(List<Question> questionList, OnQuestionClickListener OnQuestionClickListener) {
        this.questionList = questionList;
        this.OnQuestionClickListener = OnQuestionClickListener;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_view_question, parent, false);
        return new QuestionViewHolder(itemView, OnQuestionClickListener);
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
        public ImageView questionDeleteButton;
        private OnQuestionClickListener onQuestionClickListener, onQuestionDeleteClick;

        public QuestionViewHolder(@NonNull View itemView, OnQuestionClickListener onQuestionClickListener) {
            super(itemView);
            questionItemView = itemView.findViewById(R.id.it_question_view);
            questionDeleteButton = itemView.findViewById(R.id.it_question_delete);

            this.onQuestionDeleteClick = onQuestionClickListener;
            questionDeleteButton.setOnClickListener(this);

            this.onQuestionClickListener = onQuestionClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == questionDeleteButton){
                onQuestionClickListener.onQuestionDeleteClick(questionList.get(getAdapterPosition()));
            } else {
                onQuestionClickListener.onQuestionItemClick(questionList.get(getAdapterPosition()));
            }
        }
    }

    public interface OnQuestionClickListener {
        void onQuestionItemClick(Question question);
        void onQuestionDeleteClick(Question question);
    }
}