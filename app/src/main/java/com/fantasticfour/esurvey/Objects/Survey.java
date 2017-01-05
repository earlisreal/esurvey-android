package com.fantasticfour.esurvey.Objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class Survey {
    private int id;
    private int user_id;
    private String survey_title;
    private String created_at;
    private String updated_at;
    private List<SurveyPage> pages;
    private List<Response> responses = new ArrayList<>();

    public Survey(int id, int user_id, String survey_title, String created_at, String updated_at) {
        this.id = id;
        this.user_id = user_id;
        this.survey_title = survey_title;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public Survey(int id, int user_id, String survey_title, String created_at, String updated_at, List<SurveyPage> pages) {
        this.id = id;
        this.user_id = user_id;
        this.survey_title = survey_title;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.pages = pages;
    }

    public Survey(){

    }

    public int getSyncedResponseCount(){
        int count = 0;
        for (Response response : responses){
            if(response.getSynced() == 1){
                count++;
            }
        }
        return count;
    }

    public List<Response> getResponses() {
        return responses;
    }

    public void setResponses(List<Response> responses) {
        this.responses = responses;
    }

    public int getQuestionCount(){
        int count = 0;
        for(SurveyPage page : pages){
            count += page.getQuestions().size();
        }
        return count;
    }

    public List<SurveyPage> getPages() {
        return pages;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setPages(List<SurveyPage> pages) {
        this.pages = pages;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurvey_title() {
        return survey_title;
    }

    public void setSurvey_title(String survey_title) {
        this.survey_title = survey_title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
