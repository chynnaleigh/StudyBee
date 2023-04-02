package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Answer implements Serializable {
    private String answerOption;
    private String answerId;

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
}
