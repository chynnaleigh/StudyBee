package com.example.finalproject.flashcards;

import java.io.Serializable;

public class Card implements Serializable {
    private String cardSideA;
    private String cardSideB;
    private String cardId;
    private long cardTimestamp;

    public Card() {

    }

    public Card(String cardSideA, String cardSideB, String cardId) {
        this.cardSideA = cardSideA;
        this.cardSideB = cardSideB;
        this.cardId = cardId;
    }

    public String getCardSideA() {
        return cardSideA;
    }

    public void setCardSideA(String cardSideA) {
        this.cardSideA = cardSideA;
    }

    public String getCardSideB() {
        return cardSideB;
    }

    public void setCardSideB(String cardSideB) {
        this.cardSideB = cardSideB;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public long getCardTimestamp() {
        return cardTimestamp;
    }

    public void setCardTimestamp(long cardTimestamp) {
        this.cardTimestamp = cardTimestamp;
    }
}
