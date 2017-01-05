package com.fantasticfour.esurvey.Objects;

import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class ServerResponse {
    private List<Survey> surveys;
    private List<SurveyPage> survey_pages;
    private List<Question> questions;
    private List<QuestionChoice> choices;

    public ServerResponse(List<Survey> surveys, List<SurveyPage> surveyPages, List<Question> questions, List<QuestionChoice> choices) {
        this.surveys = surveys;
        this.survey_pages = surveyPages;
        this.questions = questions;
        this.choices = choices;
    }

    public List<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<Survey> surveys) {
        this.surveys = surveys;
    }

    public List<SurveyPage> getSurveyPages() {
        return survey_pages;
    }

    public void setSurveyPages(List<SurveyPage> surveyPages) {
        this.survey_pages = surveyPages;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<QuestionChoice> getChoices() {
        return choices;
    }

    public void setChoices(List<QuestionChoice> choices) {
        this.choices = choices;
    }
}
