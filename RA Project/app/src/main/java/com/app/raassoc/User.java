package com.app.raassoc;

/**
 * Created by New android on 16-11-2018.
 */

public class User {
    private String id;
    private  String username, email, gender;

    public User(String id,String username, String email) {
        this.id=id;
        this.username = username;
        this.email = email;

    }
    public String getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


}
