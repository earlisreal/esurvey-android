package com.fantasticfour.esurvey.Objects;

/**
 * Created by earl on 9/20/2016.
 */
public class QuestionChoice {
    private int id;
    private int question_id;
    private String label;

    public QuestionChoice(int id, int question_id, String label) {
        this.id = id;
        this.question_id = question_id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
