package com.app.raassoc.helper;

import com.orm.SugarRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VendorORM extends SugarRecord {

    String equipid = "";
    String vendorname = "";
    String jobstatus = "";

    String nots = "";
    String userid = "";
    String usertype = "";
    String dateid = "";
   public VendorORM()
    {

    }

   public VendorORM(String eid, String vnme, String jobStas,String nos, String uid, String utpe)
    {
        this.equipid = eid;
        this.vendorname = vnme;
        this.nots = nos;
        this.userid = uid;
        this.usertype = utpe;
        this.jobstatus = jobStas;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        Date now = new Date();
        dateid = formatter.format(now);

    }




    public String  getequipid()
    {  return  this.equipid;
    }


    public String  getVendorname()
    { return  this.vendorname;
    }


    public String  getJobstatus()
    { return  this.jobstatus;
    }

    public String  getNots()
    { return  this.nots;
    }

    public String  getUserid()
    { return  this.userid;
    }

    public String  getUsertype()
    { return  this.usertype;
    }
    public String  getDateid()
    { return  this.dateid;
    }








}
