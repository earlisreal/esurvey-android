package com.fantasticfour.esurvey.Objects;

/**
 * Created by earl on 9/19/2016.
 */
public class LoginResponse {

    private User user;
    private int status;
    private String message;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
