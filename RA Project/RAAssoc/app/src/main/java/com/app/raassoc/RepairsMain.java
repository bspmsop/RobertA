package com.app.raassoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by New android on 12-11-2018.
 */

public class RepairsMain  extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListNumbers;
    private HashMap<String,Contacto> listContactos;
    private int lastExpandPosition=-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reparis_main);
        init();
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandPosition != -1 && groupPosition != lastExpandPosition){
                    expandableListView.collapseGroup(lastExpandPosition);
                }
                lastExpandPosition =groupPosition;

            }
        });

    }
    private void init(){
        this.expandableListView= (ExpandableListView) findViewById(R.id.expandLV);
        this.listContactos = getContactos();
        this.expandableListNumbers = new ArrayList<>(listContactos.keySet());
        this.expandableListAdapter = new ExpandableListAdapter(this,expandableListNumbers,listContactos);

    }
    private HashMap<String ,Contacto> getContactos(){
        HashMap<String,Contacto> listaC = new HashMap<>();

        listaC.put("Boiler #1",new Contacto("Repairs Scheduled     09-24-2018 12:30 PM","Text","Test",R.drawable.vendor_repair));
        listaC.put("Boiler #2",new Contacto("Complete    09-01-2018 12:30 PM","Text","Test",R.drawable.vendor_repair_1));
        listaC.put("Mixed Leak Value",new Contacto("Waiting on Parts  08-22-2018  11:20 AM","Text","Test",R.drawable.vendor_repair_2));

        return listaC;
    }
}