package com.app.raassoc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by New android on 02-01-2019.
 */

public class CustomParseJSON {
    public static ArrayList<CustomSections> parseSections(String result) throws JSONException {

        Log.i("msg", "called parsing " +result);



        ArrayList<CustomSections> listSection=new ArrayList<>();
        JSONObject parent=new JSONObject(result);

        JSONArray arraySections=parent.getJSONArray("sections");

        if(arraySections!=null && arraySections.length()>0){





            for(int i=0;i<arraySections.length();i++){

                JSONObject jo=arraySections.getJSONObject(i);
                JSONArray arrayFields=jo.getJSONArray("fields");

                CustomSections section=new CustomSections();
                section.setHead(jo.getString("head"));
                ArrayList<CustomFields> listFields=new ArrayList<>();
                for(int j=0;j<arrayFields.length();j++){
                    JSONObject objField=arrayFields.getJSONObject(j);

                    CustomFields f=new CustomFields();
                    f.setId(objField.getInt("id"));
                    f.setiType(objField.getString("itype"));
                    f.setLabel(objField.getString("label"));
                    f.setRequired(objField.getString("required"));
                    f.setiOptions(objField.getString("ioptions"));
                    f.setiDefault(objField.getString("idefault"));
                    listFields.add(f);
                }
                section.setListFields(listFields);

                listSection.add(section);
            }

        }

        return listSection;
    }


    public static String parseUploadImageResponse(String result) throws JSONException {

        String output=null;

        JSONObject parent=new JSONObject(result);

        //{"scode":"200","message":"The file image.jpg has been uploaded.","filePath":"5c2580b49f45b_image.jpg"}

        String code=parent.getString("scode");

        if(code.equals("200")){
            output=parent.getString("filePath");
        }

        return output;
    }



    public static String parseUploadDataResponse(String result) throws JSONException {

        String output=null;

        JSONObject parent=new JSONObject(result);

        //{"status":200,"message":"Inspection Sheet saved successfully.","error":"false"}

        String code=parent.getString("status");

        if(code.equals("200")){
            output=parent.getString("message");
        }

        return output;
    }
}
