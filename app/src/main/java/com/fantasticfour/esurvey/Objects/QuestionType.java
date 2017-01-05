package com.fantasticfour.esurvey.Objects;

import android.view.View;

/**
 * Created by earl on 9/20/2016.
 */
public class QuestionType {
    private int id;
    private String type;
    private int has_choices;

    public QuestionType(int id, String type, int has_choices) {
        this.id = id;
        this.type = type;
        this.has_choices = has_choices;
    }

//    public View getView(){
//        switch (type){
//            case:
//        }
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHas_choices() {
        return has_choices;
    }

    public void setHas_choices(int has_choices) {
        this.has_choices = has_choices;
    }
}
