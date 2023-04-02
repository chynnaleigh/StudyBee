package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String title;
    private String quizId;

    public Quiz() {

    }

    public Quiz(String title, String quizId) {
        this.title = title;
        this.quizId = quizId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }
}
