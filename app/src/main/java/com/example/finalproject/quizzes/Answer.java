package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Answer implements Serializable {
    private String answerOption;
    private String answerId;
    private long answerTimestamp;
    private boolean isCorrect = false;
    private boolean isSelected = false;

    public Answer() {

    }

    public Answer(String answerOption, String answerId) {
        this.answerOption = answerOption;
        this.answerId = answerId;
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

    public long getAnswerTimestamp() {
        return answerTimestamp;
    }

    public void setAnswerTimestamp(long questionTimestamp) {
        this.answerTimestamp = questionTimestamp;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean correct) {
        isCorrect = correct;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean selected) {
        isSelected = selected;
    }
}
