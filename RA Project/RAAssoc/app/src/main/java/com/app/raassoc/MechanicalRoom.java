package com.app.raassoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by New android on 31-10-2018.
 */

public class MechanicalRoom extends Fragment  {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        View rootview = inflater.inflate(R.layout.mechanical_room, container, false);
        Button btnMechSign = (Button) rootview.findViewById(R.id.btnMechsign);


        btnMechSign.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start your activity here
                Intent intent = new Intent(view.getContext(), BoilerRoom.class);
                startActivity(intent);
            }
        });
        Button btnSignlog = (Button) rootview.findViewById(R.id.btnSignlog);
        btnSignlog.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Start your activity here
                Intent intent = new Intent(view.getContext(), SignInLogActivity.class);
                startActivity(intent);
            }
        });
        return rootview;
        //return view;
        // return inflater.inflate(R.layout.mechanical_room, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Mechanical Room");
    }

}
