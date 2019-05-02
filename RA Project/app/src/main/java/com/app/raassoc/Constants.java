package com.app.raassoc;

import android.content.Context;
import android.util.Log;

import com.app.raassoc.helper.NetworkChangeReceiver;

/**
 * Created by New android on 26-10-2018.
 */

public class Constants {


    public static final String URL_BASIC="http://mybets360.com/";
    //public static final String URL_BASIC= "http://v1-1.mybets360.com/";

    public static final String URL_bBASIC= Constants.URL_BASIC;




    private static final String ROOT_URL="http://design.ratheassociates.com/testapi/";
    public static final String URL_REGISTER=ROOT_URL+"regiAPI1.php";
    public static final String URL_LOGIN= URL_BASIC + "ulogin";
    public static final String URL_REGISTER1="http://rreg.in/admin/regAPI.php";
    public static final String URL_FORGETPASSWORD= URL_BASIC +"savenewForgot";
    public static final String URL_OFFLINE= URL_BASIC + "getallinformation/";


    //Super Buildings List
    public static final String DATA_URL = URL_BASIC + "allbuildingsbyuser/";
    //JSON array name
    public static final String JSON_ARRAY = "response";
    public static final String TAG_USERNAME = "title";
    public static final String TAG_NAME = "id";


    //Super MechanicalRoom List
    public static final String MECH_URL = URL_BASIC + "allmechsbybuilding/";
    public static final String MECH_ARRAY = "response";
    public static final String MECH_TITL = "title";
    public static final String MECH_ID = "id";
    public static  String GMechanical_ID = "0";
    public static   String GMechanical_Title = "Mechanical Room";

    public static final String INS_SAVE = URL_BASIC + "saveinspectionsheet";

    //Super Boiler Sign_in
    public static final String BOILER_URL = URL_BASIC + "mechviewbyid/";
    public static final String BOILER_ARRAY = "equipments";

    //Super Vendor Repairs Save
    public static final String SAVE_VENDOR= URL_BASIC + "savenewVendor";

    //Custom Inspection
    public static final String CUST_INSP = URL_BASIC + "getapiinspectionform/";
    public static final String CUST_EFF = URL_BASIC + "getapieqpform/";
    public static final String CUST_SAVEEFF = URL_BASIC + "saveeffeciency";


    public static final String CUST_EQD = URL_BASIC  + "eqpdocuments/";

    public static final String IMAGE_UPLOAD_URL = URL_BASIC + "upload";

    //Save Inspection
    public static final String SAVE_INSP= URL_BASIC + "saveinspectionsheet";

    //Vendor Listing
    public static final String VEND_LIST = URL_BASIC +  "getvendorinformation/";

    //Update Listing Vendor
    public static final String UPDATE_VEN = URL_BASIC +  "updatevendorrecord";

    //Document Library list
    public static final String DOC_LIST= URL_BASIC + "mechdocuments/";







    public static int convertDp(Context con, int amt)
    {
if (amt > 3) {

    final float scale = con.getResources().getDisplayMetrics().density;
    int pixels = (int) (amt * scale + 2.5f);

    return pixels;
}
else
{
    final float scale = con.getResources().getDisplayMetrics().density;
    int pixels = (int) (amt * scale );

    return pixels;

}

    }




}
