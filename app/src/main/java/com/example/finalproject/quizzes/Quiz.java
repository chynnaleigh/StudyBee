package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Quiz implements Serializable {
    private String quizTitle;
    private String quizId;
    private long quizTimestamp;
    private int questionCount = 0;

    public Quiz() {

    }

    public Quiz(String quizTitle, String quizId, int questionCount) {
        this.quizTitle = quizTitle;
        this.quizId = quizId;
        this.questionCount = questionCount;
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

    public long getQuizTimestamp() {
        return quizTimestamp;
    }

    public void setQuizTimestamp(long quizTimestamp) {
        this.quizTimestamp = quizTimestamp;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}
