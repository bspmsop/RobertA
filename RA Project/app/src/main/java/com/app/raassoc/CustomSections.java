package com.app.raassoc;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by New android on 02-01-2019.
 */

public class CustomSections implements Serializable{

    private String head;
    private int sort;
    private int sectionId;
    private String cols;
    private ArrayList<CustomFields> listFields;

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getCols() {
        return cols;
    }

    public void setCols(String cols) {
        this.cols = cols;
    }

    public ArrayList<CustomFields> getListFields() {
        return listFields;
    }

    public void setListFields(ArrayList<CustomFields> listFields) {
        this.listFields = listFields;
    }
}
