package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String quizTitle;
    private String quizId;
    private String quizTimestamp;

    public Quiz() {

    }

    public Quiz(String quizTitle, String quizId, String quizTimestamp) {
        this.quizTitle = quizTitle;
        this.quizId = quizId;
        this.quizTimestamp = quizTimestamp;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getQuizTimestamp() {
        return quizTimestamp;
    }

    public void setQuizTimestamp(String quizTimestamp) {
        this.quizTimestamp = quizTimestamp;
    }
}
