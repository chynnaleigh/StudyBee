package com.example.finalproject.quizzes;

import java.io.Serializable;

public class Question implements Serializable {
    private String question;
    private String questionId;
    private int correctAnswerCount = 0;
    private int answerCount = 0;

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

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void setCorrectAnswerCount(int correctAnswersCount) {
        this.correctAnswerCount = correctAnswersCount;
    }
}
