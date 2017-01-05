package com.fantasticfour.esurvey.Objects;

/**
 * Created by earl on 10/4/2016.
 */

public class QuestionOption {
    int id;
    int question_id;
    int max_rating;


    public QuestionOption(int id, int question_id, int max_rating) {
        this.id = id;
        this.question_id = question_id;
        this.max_rating = max_rating;
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

    public int getMax_rating() {
        return max_rating;
    }

    public void setMax_rating(int max_rating) {
        this.max_rating = max_rating;
    }
}
