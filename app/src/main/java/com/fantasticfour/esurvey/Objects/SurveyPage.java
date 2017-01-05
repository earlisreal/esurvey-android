package com.fantasticfour.esurvey.Objects;

import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class SurveyPage {
    private int id;
    private int survey_id;
    private int page_no;
    private String page_title;
    private String page_description;
    private List<Question> questions;

    public SurveyPage(int id, int survey_id, int page_no, String page_title, String page_description) {
        this.id = id;
        this.survey_id = survey_id;
        this.page_no = page_no;
        this.page_title = page_title;
        this.page_description = page_description;
    }

    public SurveyPage(int id, int survey_id, int page_no, String page_title, String page_description, List<Question> questions) {
        this.id = id;
        this.survey_id = survey_id;
        this.page_no = page_no;
        this.page_title = page_title;
        this.page_description = page_description;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public int getPage_no() {
        return page_no;
    }

    public void setPage_no(int page_no) {
        this.page_no = page_no;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public String getPage_description() {
        return page_description;
    }

    public void setPage_description(String page_description) {
        this.page_description = page_description;
    }
}
