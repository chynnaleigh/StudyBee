package com.example.finalproject.flashcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class FlashcardSetAdapter extends RecyclerView.Adapter<FlashcardSetAdapter.FlashcardSetViewHolder>{
    private List<Flashcard> flashcardList;
    private OnFlashcardSetClickListener onFlashcardSetClickListener;

    public FlashcardSetAdapter(List<Flashcard> flashcardList, OnFlashcardSetClickListener onFlashcardSetClickListener) {
        this.flashcardList = flashcardList;
        this.onFlashcardSetClickListener = onFlashcardSetClickListener;
    }

    @NonNull
    @Override
    public FlashcardSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_flashcard_set, parent, false);
        return new FlashcardSetViewHolder(itemView, onFlashcardSetClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardSetViewHolder holder, int position) {
        Flashcard flashcard = flashcardList.get(position);
        holder.flashcardSetItem.setText(flashcard.getFlashcardSetTitle());
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public class FlashcardSetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView flashcardSetItem;
        private OnFlashcardSetClickListener onFlashcardSetClickListener;

        public FlashcardSetViewHolder(@NonNull View itemView, OnFlashcardSetClickListener onFlashcardSetClickListener) {
            super(itemView);
            flashcardSetItem = itemView.findViewById(R.id.it_flashcard_view);
            this.onFlashcardSetClickListener = onFlashcardSetClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onFlashcardSetClickListener.onFlashcardSetItemClick(flashcardList.get(getAdapterPosition()));
        }
    }

    public interface OnFlashcardSetClickListener {
        void onFlashcardSetItemClick(Flashcard flashcard);
    }
}
