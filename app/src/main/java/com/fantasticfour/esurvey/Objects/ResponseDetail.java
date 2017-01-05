package com.fantasticfour.esurvey.Objects;

/**
 * Created by earl on 9/20/2016.
 */
public class ResponseDetail {
    private int id;
    private int response_id;
    private int question_id;
    private String text_answer;
    private int choice_id;
    private int row_id;

    public ResponseDetail(int response_id, int question_id, String text_answer, int choice_id, int row_id) {
        this.response_id = response_id;
        this.question_id = question_id;
        this.text_answer = text_answer;
        this.choice_id = choice_id;
        this.row_id = row_id;
    }

    public ResponseDetail(int response_id, int question_id, String text_answer, int choice_id) {
        this.response_id = response_id;
        this.question_id = question_id;
        this.text_answer = text_answer;
        this.choice_id = choice_id;
    }

    public int getRow_id() {
        return row_id;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResponse_id() {
        return response_id;
    }

    public void setResponse_id(int response_id) {
        this.response_id = response_id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getText_answer() {
        return text_answer;
    }

    public void setText_answer(String text_answer) {
        this.text_answer = text_answer;
    }

    public int getChoice_id() {
        return choice_id;
    }

    public void setChoice_id(int choice_id) {
        this.choice_id = choice_id;
    }
}
