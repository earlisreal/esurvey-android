package com.fantasticfour.esurvey.Objects;

import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class Question {
    private int id;
    private int survey_page_id;
    private int question_type_id;
    private String question_title;
    private int order_no;
    private int is_mandatory;
    private List<QuestionChoice> choices;
    private List<QuestionRow> rows;
    private QuestionType question_type;
    private QuestionOption option;

    public Question(int id, int survey_page_id, int question_type_id, String question_title, int order_no, int is_mandatory, List<QuestionChoice> choices, List<QuestionRow> rows, QuestionType question_type, QuestionOption option) {
        this.id = id;
        this.survey_page_id = survey_page_id;
        this.question_type_id = question_type_id;
        this.question_title = question_title;
        this.order_no = order_no;
        this.is_mandatory = is_mandatory;
        this.choices = choices;
        this.rows = rows;
        this.question_type = question_type;
        this.option = option;
    }

    public Question(int id, int survey_page_id, int question_type_id, String question_title, int order_no, int is_mandatory) {
        this.id = id;
        this.survey_page_id = survey_page_id;
        this.question_type_id = question_type_id;
        this.question_title = question_title;
        this.order_no = order_no;
        this.is_mandatory = is_mandatory;
    }

    public Question(int id, int survey_page_id, int question_type_id, String question_title, int order_no, int is_mandatory, QuestionType question_type, List<QuestionChoice> choices) {
        this.id = id;
        this.survey_page_id = survey_page_id;
        this.question_type_id = question_type_id;
        this.question_title = question_title;
        this.order_no = order_no;
        this.is_mandatory = is_mandatory;
        this.question_type = question_type;
        this.choices = choices;
    }

    public Question(int id, int survey_page_id, int question_type_id, String question_title, int order_no, int is_mandatory, QuestionType question_type, List<QuestionChoice> choices, QuestionOption option) {
        this.id = id;
        this.survey_page_id = survey_page_id;
        this.question_type_id = question_type_id;
        this.question_title = question_title;
        this.order_no = order_no;
        this.is_mandatory = is_mandatory;
        this.question_type = question_type;
        this.choices = choices;
        this.option = option;
    }

    public List<QuestionRow> getRows() {
        return rows;
    }

    public void setRows(List<QuestionRow> rows) {
        this.rows = rows;
    }

    public QuestionOption getOption() {
        return option;
    }

    public void setOption(QuestionOption option) {
        this.option = option;
    }

    public QuestionType getQuestion_type() {
        return question_type;
    }

    public void setQuestion_type(QuestionType question_type) {
        this.question_type = question_type;
    }

    public List<QuestionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionChoice> choices) {
        this.choices = choices;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSurvey_page_id() {
        return survey_page_id;
    }

    public void setSurvey_page_id(int survey_page_id) {
        this.survey_page_id = survey_page_id;
    }

    public int getQuestion_type_id() {
        return question_type_id;
    }

    public void setQuestion_type_id(int question_type_id) {
        this.question_type_id = question_type_id;
    }

    public String getQuestion_title() {
        return question_title;
    }

    public void setQuestion_title(String question_title) {
        this.question_title = question_title;
    }

    public int getOrder_no() {
        return order_no;
    }

    public void setOrder_no(int order_no) {
        this.order_no = order_no;
    }

    public int getIs_mandatory() {
        return is_mandatory;
    }

    public void setIs_mandatory(int is_mandatory) {
        this.is_mandatory = is_mandatory;
    }
}
