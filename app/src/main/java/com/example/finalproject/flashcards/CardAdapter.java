package com.example.finalproject.flashcards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder>{
    private List<Card> cardList;
    private OnCardClickListener onCardClickListener;

    public CardAdapter(List<Card> cardList, OnCardClickListener onCardClickListener) {
        this.cardList = cardList;
        this.onCardClickListener = onCardClickListener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_card, parent, false);
        return new CardViewHolder(itemView, onCardClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.cardItemViewA.setText(card.getCardSideA());
        holder.cardItemViewB.setText(card.getCardSideB());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView cardItemViewA, cardItemViewB;
        private OnCardClickListener onCardClickListener;

        public CardViewHolder(@NonNull View itemView, OnCardClickListener onCardClickListener) {
            super(itemView);
            cardItemViewA = itemView.findViewById(R.id.it_sideA_view);
            cardItemViewB = itemView.findViewById(R.id.it_sideB_view);
            this.onCardClickListener = onCardClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCardClickListener.onCardItemClick(cardList.get(getAdapterPosition()));
        }
    }

    public interface OnCardClickListener {
        void onCardItemClick(Card card);
    }
}
