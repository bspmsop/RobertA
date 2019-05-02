package com.app.raassoc;

/**
 * Created by New android on 31-12-2018.
 */

public class MyBets {

    private String id;
    private String vname;
    private String status;
    private String erepaired;
    private String daterep;
    private String notes;

    private boolean isVisible = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErepaired() {
        return erepaired;
    }

    public void setErepaired(String erepaired) {
        this.erepaired = erepaired;
    }

    public String getDaterep() {
        return daterep;
    }

    public void setDaterep(String daterep) {
        this.daterep = daterep;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}