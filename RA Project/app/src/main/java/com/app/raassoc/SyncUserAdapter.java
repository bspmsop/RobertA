package com.app.raassoc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by New android on 31-12-2018.
 */

public class SyncUserAdapter extends ArrayAdapter<SyncUser> {
    //storing all the names in the list
    private List<SyncUser> names;

    //context object
    private Context context;

    //constructor
    public SyncUserAdapter(Context context, int resource, List<SyncUser> names) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.sync_names, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        ImageView imageViewStatus = (ImageView) listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        SyncUser mUser = names.get(position);

        //setting the name to textview
        textViewName.setText(mUser.getName());

        if (mUser.getStatus() == 0)
            imageViewStatus.setBackgroundResource(android.R.color.holo_orange_dark);
        else
            imageViewStatus.setBackgroundResource(android.R.color.black);
        return listViewItem;
    }
}