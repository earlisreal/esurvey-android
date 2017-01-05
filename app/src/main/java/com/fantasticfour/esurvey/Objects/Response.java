package com.fantasticfour.esurvey.Objects;

import java.util.List;

/**
 * Created by earl on 9/20/2016.
 */
public class Response {
    private int id;
    private int survey_id;
    private String created_at;
    private List<ResponseDetail> responseDetails;
    private int synced;

    public Response(int survey_id, String created_at) {
        this.survey_id = survey_id;
        this.created_at = created_at;
    }

    public Response(int id, int survey_id, String created_at, int synced) {
        this.id = id;
        this.survey_id = survey_id;
        this.synced = synced;
        this.created_at = created_at;
    }

    public Response(String created_at, int synced) {
        this.synced = synced;
        this.created_at = created_at;
    }

    public Response(int survey_id){
        this.survey_id = survey_id;
    }

    public Response(){

    }

    public int getSynced() {
        return synced;
    }

    public void setSynced(int synced) {
        this.synced = synced;
    }

    public List<ResponseDetail> getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(List<ResponseDetail> responseDetails) {
        this.responseDetails = responseDetails;
    }

    public int getId() {
        return id;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
