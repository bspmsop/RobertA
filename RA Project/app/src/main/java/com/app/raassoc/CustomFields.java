package com.app.raassoc;

import java.io.Serializable;

/**
 * Created by New android on 02-01-2019.
 */

public class CustomFields  implements Serializable{
    private int id;
    private int template_id;
    private int sectionId;
    private String label;
    private String iType;
    private String required;
    private String iOptions;
    private String iDefault;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(int template_id) {
        this.template_id = template_id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getiType() {
        return iType;
    }

    public void setiType(String iType) {
        this.iType = iType;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getiOptions() {
        return iOptions;
    }

    public void setiOptions(String iOptions) {
        this.iOptions = iOptions;
    }

    public String getiDefault() {
        return iDefault;
    }

    public void setiDefault(String iDefault) {
        this.iDefault = iDefault;
    }
}
