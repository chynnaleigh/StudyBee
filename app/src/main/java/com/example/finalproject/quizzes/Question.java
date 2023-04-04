package com.example.finalproject.quizzes;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String question;
    private String questionId;
    private long questionTimestamp;
    private int correctAnswerCount = 0;

    public Question() {

    }

    public Question(String question, String questionId, long questionTimestamp, int correctAnswerCount) {
        this.question = question;
        this.questionId = questionId;
        this.questionTimestamp = questionTimestamp;
        this.correctAnswerCount = correctAnswerCount;
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

    public long getQuestionTimestamp() {
        return questionTimestamp;
    }

    public void setQuestionTimestamp(long questionTimestamp) {
        this.questionTimestamp = questionTimestamp;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(int correctAnswersCount) {
        this.correctAnswerCount = correctAnswersCount;
    }
}
