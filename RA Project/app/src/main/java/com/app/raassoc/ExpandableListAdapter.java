package com.app.raassoc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by New android on 12-11-2018.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listTitle;
    private HashMap<String,Contacto> expandableListDetails;

    public ExpandableListAdapter(Context context, List<String> listTitle, HashMap<String, Contacto> expandableListDetails) {
        this.context = context;
        this.listTitle = listTitle;
        this.expandableListDetails = expandableListDetails;
    }

    @Override
    public int getGroupCount() {
        return this.listTitle.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listTitle.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListDetails.get(this.listTitle.get(groupPosition));

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String number= (String) getGroup(groupPosition);
        Contacto contacto = (Contacto) getChild(groupPosition,0);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView =layoutInflater.inflate(R.layout.activity_repairs, null);

        }

        TextView tvHeader = (TextView) convertView.findViewById(R.id.header_tv);
        TextView tvDate1 = (TextView) convertView.findViewById(R.id.date1);
        ImageView vendorTV = (ImageView) convertView.findViewById(R.id.vendorIV);
        tvHeader.setText(number);
        tvDate1.setText(contacto.getNumero());
        vendorTV.setImageResource(contacto.getImg());
        if (isExpanded) {
            tvHeader.setTypeface(null, Typeface.BOLD);
            tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.up_arrow, 0);
        } else {
            // If group is not expanded then change the text back into normal
            // and change the icon

            tvHeader.setTypeface(null, Typeface.NORMAL);
            tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.down_arrow, 0);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Contacto contacto = (Contacto)getChild(groupPosition,childPosition);
        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView =layoutInflater.inflate(R.layout.activity_repairs_chil, null);

        }
        //LinearLayout layoutId =convertView.findViewById(R.id.textId);
        // TextView textVId =convertView.findViewById(R.id.textId);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
