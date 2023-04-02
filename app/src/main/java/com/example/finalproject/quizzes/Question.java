package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Question implements Serializable {
    private String question;
    private String questionId;

    public Question() {

    }

    public Question(String question, String questionId) {
        this.question = question;
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
}
