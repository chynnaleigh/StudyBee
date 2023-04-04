package com.example.finalproject.flashcards;

import java.io.Serializable;

public class Flashcard implements Serializable {
    private String flashcardSetTitle;
    private String flashcardSetId;
    private long flashcardSetTimestamp;
    private int cardCount = 0;

    public Flashcard() {

    }

    public Flashcard(String flashcardSetTitle, String flashcardSetId, int cardCount) {
        this.flashcardSetTitle = flashcardSetTitle;
        this.flashcardSetId = flashcardSetId;
        this.cardCount = cardCount;
    }

    public String getFlashcardSetTitle() {
        return flashcardSetTitle;
    }

    public void setFlashcardSetTitle(String flashcardSetTitle) {
        this.flashcardSetTitle = flashcardSetTitle;
    }

    public String getFlashcardSetId() {
        return flashcardSetId;
    }

    public void setFlashcardSetId(String flashcardSetId) {
        this.flashcardSetId = flashcardSetId;
    }

    public long getFlashcardSetTimestamp() {
        return flashcardSetTimestamp;
    }

    public void setFlashcardSetTimestamp(long flashcardSetTimestamp) {
        this.flashcardSetTimestamp = flashcardSetTimestamp;
    }

    public int getCardCount() {
        return cardCount;
    }

    public void setCardCount(int cardCount) {
        this.cardCount = cardCount;
    }

}
