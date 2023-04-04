package com.example.finalproject.flashcards;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.example.finalproject.R;

import java.util.List;

public class CardPagerAdapter extends PagerAdapter {
    private List<Card> cardList;
    private Context context;
    private SparseArray<CardViewHolder> cardViewHolderSparseArray;
    private int currentPosition = 0;
    private LayoutInflater layoutInflater;

    public CardPagerAdapter(Context context, List<Card> cardList) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.cardList = cardList;
        cardViewHolderSparseArray = new SparseArray<>(cardList.size());

    }

    @Override
    public int getCount() {
        return cardList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.card_view_flashcards, container, false);
//        Card card = cardList.get(position);
        CardViewHolder holder = new CardViewHolder(view);
        holder.bindData(cardList.get(position));
        container.addView(view);
        cardViewHolderSparseArray.put(position, holder);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        cardViewHolderSparseArray.remove(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        int index = cardViewHolderSparseArray.indexOfValue((CardViewHolder) object);
        if(index == -1) {
            return POSITION_NONE;
        } else {
            return index;
        }
    }

    public void flipCard() {
        CardViewHolder holder = cardViewHolderSparseArray.get(currentPosition);
        if(holder != null) {
            holder.flipCard();
        }
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        currentPosition = position;
    }

    public void swipeLeft() {
        if(currentPosition < getCount() - 1) {
            currentPosition++;
            notifyDataSetChanged();
        }
    }

    public void swipeRight() {
        if(currentPosition > 0) {
            currentPosition--;
            notifyDataSetChanged();
        }
    }

    private class CardViewHolder {
        private CardView sideAView, sideBView;
        private TextView sideAViewText, sideBViewText, sideBCheckViewText;
        private Button sideBViewYesButton, sideBViewNoButton;

        CardViewHolder(View itemView) {
            sideAView = itemView.findViewById(R.id.flashcard_card_viewA);
            sideBView = itemView.findViewById(R.id.flashcard_card_viewB);

            sideAView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCard();
                }
            });
            sideBView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flipCard();
                }
            });
        }

        void bindData(Card card) {
            sideAViewText = sideAView.findViewById(R.id.card_view_sideA);
            sideBViewText = sideBView.findViewById(R.id.card_view_sideB);
            sideBCheckViewText = sideBView.findViewById(R.id.card_view_check_text);
            sideBViewYesButton = sideBView.findViewById(R.id.card_view_yes_button);
            sideBViewNoButton = sideBView.findViewById(R.id.card_view_no_button);

            sideAViewText.setText(card.getCardSideA());
            sideBViewText.setText(card.getCardSideB());

        }

        void flipCard() {
            if(sideBView.getVisibility() == View.VISIBLE) {
                sideAView.setVisibility(View.VISIBLE);
                sideBView.setVisibility(View.GONE);
            } else {
                sideAView.setVisibility(View.GONE);
                sideBView.setVisibility(View.VISIBLE);
            }
        }
    }
}
