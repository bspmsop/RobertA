package com.app.raassoc;

/**
 * Created by New android on 04-01-2019.
 */

public class SyncUser {
    private String name;
    private int status;

    public SyncUser(){

    }
    public SyncUser(String name, int status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

