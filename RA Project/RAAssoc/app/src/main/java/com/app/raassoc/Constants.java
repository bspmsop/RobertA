package com.app.raassoc;

/**
 * Created by New android on 26-10-2018.
 */

public class Constants {

    private static final String ROOT_URL="http://design.ratheassociates.com/testapi/";
    public static final String URL_REGISTER=ROOT_URL+"regiAPI1.php";
    public static final String URL_LOGIN="http://mybets360.com/ulogin";

    //Super Buildings List
    public static final String DATA_URL = "http://mybets360.com/allbuildingsbyuser/";
    //JSON array name
    public static final String JSON_ARRAY = "response";
    public static final String TAG_USERNAME = "title";
    public static final String TAG_NAME = "id";

    //Super MechanicalRoom List
    public static final String MECH_URL = "http://mybets360.com/allmechsbybuilding/";
    public static final String MECH_ARRAY = "response";
    public static final String MECH_TITL = "title";
    public static final String MECH_ID = "id";

    //Super Boiler Sign_in
    public static final String BOILER_URL = "http://mybets360.com/mechviewbyid/";
    public static final String BOILER_ARRAY = "equipments";

}
