package com.app.raassoc;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.lang.Integer.parseInt;

/**
 * Created by New android on 31-12-2018.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> implements View.OnClickListener {

    private Context context;
    private List<MyBets> myBetsList;
    LinearLayout llBtn;
    public int expand;
    private ArrayList<MyBets> buplist;


    public HomeAdapter(Context context, List<MyBets> betList) {
        this.myBetsList = betList;
        this.context = context;
        this.buplist =  new ArrayList<MyBets>();
        this.buplist.addAll(betList);

    }


    @Override
    public int getItemCount() {
        return myBetsList.size();
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bets, parent, false);
        return new HomeHolder(v);
    }

    @Override
    public void onBindViewHolder(final HomeHolder holder, final int position) {
        final MyBets bets = myBetsList.get(position);
      //  holder.mSpinner.setSelection(bets.getStatus());
        switch(bets.getStatus()) {
            case "Inspection Required":
                holder.mSpinner.setSelection(1);
                break;
            case "In progress":
                holder.mSpinner.setSelection(2);
                break;
            case "Waiting on parts":
                holder.mSpinner.setSelection(3);
                break;
            case "Repair Scheduled":
                holder.mSpinner.setSelection(4);
                break;
            case "Complete":
                holder.mSpinner.setSelection(5);
                break;
            case "On Hold/Cancelled":
                holder.mSpinner.setSelection(6);
                break;
                default:
                    holder.mSpinner.setSelection(0);
                    break;
        }
        holder.mSpinner.setId(position);
        holder.mTxtName.setText(bets.getErepaired());
        holder.mTxtDescription.setText(bets.getStatus() + "   " + bets.getDaterep());
        holder.mdateTV.setText(bets.getDaterep());
        holder.mrepairTV.setText(bets.getNotes());
        holder.mEdtStatus.setText(bets.getNotes());
        holder.mEdtStatus.setId( 35 +position);
        holder.mLayoutChild.setVisibility(View.GONE);
        holder.mImgCollp.setVisibility(View.GONE);
        holder.mImgExpand.setVisibility(View.VISIBLE);

        if(bets.getStatus().equals("Inspection Required")){
            holder.mImgStatus.setImageResource(R.drawable.redv);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        }
        else if(bets.getStatus().equals("In progress")){
            holder.mImgStatus.setImageResource(R.drawable.greenv);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorGr));
        }
        else if(bets.getStatus().equals("Complete")){
            holder.mImgStatus.setImageResource(R.drawable.vendor_repair_1);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
        }
        else if(bets.getStatus().equals("Repair Scheduled")){
            holder.mImgStatus.setImageResource(R.drawable.vendor_repair);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorBl));
        }
        else if(bets.getStatus().equals("Waiting on parts")){
            holder.mImgStatus.setImageResource(R.drawable.vendor_repair_2);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorOr));
        }
        else if(bets.getStatus().equals("On Hold/Cancelled")){
            holder.mImgStatus.setImageResource(R.drawable.gray);
            holder.mTxtDescription.setTextColor(ContextCompat.getColor(context, R.color.colorGy));
        }
        else
        {

        }
        if (bets.isVisible()) {
            holder.mLayoutChild.setVisibility(View.VISIBLE);
            Log.i("TAG","VISIBLE");
            RepairsMain.holderSelectd = bets;
            RepairsMain.selectdRow = position;
           // Log.i("TAG","holder" + holder.mTxtName.getText());
            if(llBtn != null) {
                expand = position;


                Log.i("msg" ,"status of cell is " + bets.getStatus() );

                int btnHt = Constants.convertDp(context,50);
                llBtn.getLayoutParams().height = btnHt;
                llBtn.requestLayout();




            }

            holder.mImgExpand.setVisibility(View.GONE);
            holder.mImgCollp.setVisibility(View.VISIBLE);
        } else {
            holder.mLayoutChild.setVisibility(View.GONE);

            if(llBtn != null && position == expand) {


                llBtn.getLayoutParams().height = 0;
                llBtn.requestLayout();
            }
            holder.mImgExpand.setVisibility(View.VISIBLE);
            holder.mImgCollp.setVisibility(View.GONE);
        }

        holder.mRelativeParent.setTag(position);
    }
    public void getBtn(LinearLayout disBtn,RepairsMain mRepair){
        llBtn = disBtn;
        Log.i("TAG","displayButton");
    }

    @Override
    public void onClick(View v) {


        Log.i("msg", "Im clicked ");




    }

    public void clikdedhere(View v)
    {
        //selectdRow=v.getId();
        // Toast.makeText(getApplicationContext(),"Select ID:"+ selectdRow,Toast.LENGTH_LONG).show();
        switch (v.getId()) {
            case R.id.rl_parent:
                int position = parseInt(v.getTag().toString());

                for (int i = 0; i < myBetsList.size(); i++) {
                    if (i != position) {
                        myBetsList.get(i).setVisible(false);
                    }
                }

                if (myBetsList.get(position).isVisible()) {
                    myBetsList.get(position).setVisible(false);
                } else {
                    myBetsList.get(position).setVisible(true);
                }
                notifyDataSetChanged();

                break;
        }
    }

    class HomeHolder extends RecyclerView.ViewHolder {

        RelativeLayout mRelativeParent;
        TextView mTxtName;
        TextView mTxtDescription;
        LinearLayout mLayoutChild;
        TextView mdateTV,mrepairTV;
        // TextView mTxtStatus;
        EditText mEdtStatus;
        Spinner mSpinner;
        ImageView mImgStatus,mImgExpand,mImgCollp;
       // LinearLayout llBtn;

        HomeHolder(View itemView) {
            super(itemView);

            mRelativeParent = (RelativeLayout) itemView.findViewById(R.id.rl_parent);
            mTxtName = (TextView) itemView.findViewById(R.id.tv_txtName);
            mTxtDescription = (TextView) itemView.findViewById(R.id.tv_txtDescription);
            mSpinner   = (Spinner) itemView.findViewById(R.id.spinner);

            mLayoutChild = (LinearLayout) itemView.findViewById(R.id.ll_child);
//            mTxtStatus = itemView.findViewById(R.id.tv_txtStatus);
            mEdtStatus = (EditText) itemView.findViewById(R.id.et_edtStatus);
            mImgStatus  =  (ImageView) itemView.findViewById(R.id.iv_imgProfile);
            mImgExpand =(ImageView) itemView.findViewById(R.id.iv_expand);
            mImgCollp  =(ImageView) itemView.findViewById(R.id.iv_collap);
            mdateTV    =(TextView)  itemView.findViewById(R.id.dateTV);
            mrepairTV  =(TextView) itemView.findViewById(R.id.repairTV);
           // llBtn   =(View) itemView.findViewById(R.id.llBtn);
        }
    }



    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        Log.i("msg", "im added heree");
        myBetsList.clear();
        if (charText.length() == 0) {
            myBetsList = buplist;
            this.buplist = new ArrayList<MyBets>();
            this.buplist.addAll(myBetsList);

        } else {

            Log.i("msg", "array data is " + buplist);
            for (MyBets wp : buplist) {
                if (wp.getErepaired().toLowerCase(Locale.getDefault()).contains(charText)) {
                    Log.i("msg", "im added heree");
                    myBetsList.add(wp);
                }
                if (wp.getStatus().toLowerCase(Locale.getDefault()).contains(charText)) {
                    Log.i("msg", "im added heree");
                    myBetsList.add(wp);
                }


            }
        }
        notifyDataSetChanged();
    }


}