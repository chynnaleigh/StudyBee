package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Answer implements Serializable {
    private String answerOption;
    private String answerId;
    private boolean isCorrect = false;
    private String letter;

    public Answer() {

    }

    public Answer(String answerOption, String answerId, String letter) {
        this.answerOption = answerOption;
        this.answerId = answerId;
        this.letter = letter;
    }

    public String getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean correct) {
        isCorrect = correct;
    }
}
