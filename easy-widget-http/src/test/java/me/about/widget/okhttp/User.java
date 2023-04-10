package me.about.widget.okhttp;

import java.io.Serializable;


public class User implements Serializable {

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;


    public User(String username) {
        this.username = username;
    }
}
