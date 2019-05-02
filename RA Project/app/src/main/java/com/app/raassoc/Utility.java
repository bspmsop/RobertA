package com.app.raassoc;

import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import static android.content.ContentValues.TAG;

/**
 * Created by New android on 12-01-2019.
 */

public class Utility {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
        Log.d(TAG, "heightdesired" +listAdapter.getCount());
        for (int i = 0; i < listAdapter.getCount(); i++) {
            Log.d(TAG, "ivalue=" +i);

            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
            Log.d(TAG, "getMeasure" +listItem.getMeasuredHeight());
        }
        Log.d(TAG, "height=" +totalHeight);

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        Log.d(TAG, "total=" +params.height);
        listView.requestLayout();
    }

}
