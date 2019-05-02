package com.app.raassoc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by New android on 19-01-2019.
 */

public class ListDocAdapter extends ArrayAdapter<ListDoc> {
    //the list values in the List of type hero
    List<ListDoc> docmList;
    private ArrayList<ListDoc> arraylist;
    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public ListDocAdapter(Context context, int resource, List<ListDoc> heroList) {
        super(context, resource, heroList);
        this.context = context;
        this.resource = resource;
        this.docmList = heroList;
        this.arraylist = new ArrayList<ListDoc>();
        this.arraylist.addAll(docmList);
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
        TextView textViewName = (TextView)view.findViewById(R.id.textViewName);
        TextView textViewTeam =(TextView) view.findViewById(R.id.textViewTeam);
        TextView tvDate       =(TextView) view.findViewById(R.id.tvDate);

        //getting the document of the specified position
        ListDoc hero = docmList.get(position);

        //adding values to the list item
        imageView.setImageDrawable(context.getResources().getDrawable(hero.getBuildImg()));
        textViewName.setText(hero.getFile());
        textViewTeam.setText(hero.getFilename());
        tvDate.setText(hero.getDate());

        //finally returning the view
        return view;
    }




    public  void onclickedRow(Integer pos)
    {
        Intent intent = new Intent(context, DocumentLFile.class);
        ListDoc hero = docmList.get(pos);

        intent.putExtra("path_file", hero.getPath());
        Log.i("msg", "called url " + hero.getPath());
        context.startActivity(intent);





    }
    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        docmList.clear();
        if (charText.length() == 0) {
            docmList.addAll(arraylist);
        } else {
            for (ListDoc wp : arraylist) {
                if (wp.getFile().toLowerCase(Locale.getDefault()).contains(charText)) {
                    docmList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}
