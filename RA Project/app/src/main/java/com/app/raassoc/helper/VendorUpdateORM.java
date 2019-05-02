package com.app.raassoc.helper;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VendorUpdateORM extends SugarRecord {

    String vendorid= "";
    String jobstatus = "";
    String nots = "";
    String usertype = "";
    String dateid = "";


    public  VendorUpdateORM()
    {

    }

    public VendorUpdateORM(String vid, String jostaus, String not, String utype)
    {

        this.vendorid = vid;
        this.jobstatus = jostaus;
        this.nots = not;
        this.usertype = utype;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        dateid = formatter.format(now);


    }


    public String getVendorid()
    {

        return this.vendorid;
    }
    public String getJobstatus()
    {

        return this.jobstatus;
    }
    public String getNots()
    {

        return this.nots;
    }
    public String getUsertype()
    {

        return this.usertype;
    }
    public String getDateid()
    {

        return this.dateid;
    }









}
