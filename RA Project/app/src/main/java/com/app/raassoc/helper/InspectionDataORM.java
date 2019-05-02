package com.app.raassoc.helper;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InspectionDataORM extends SugarRecord {

    public  InspectionDataORM()
    {

    }

    String userid = "";
    String usertype = "";
    String isins = "";
    String dateid = "";
    String myinsdata = "";

    public InspectionDataORM(String uid, String utype, String isins, String myinsta)
    {
        this.userid = uid;
        this.usertype = utype;
        this.isins = isins;
        this.myinsdata = myinsta;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        dateid = formatter.format(now);



    }


    public String getUserid()
    {
        return  this.userid;
    }
    public String getUsertype()
    {
        return  this.usertype;
    }
    public String getIsins()
    {
        return  this.isins;
    }
    public String getMyinsdata()
    {
        return  this.myinsdata;
    }






}
