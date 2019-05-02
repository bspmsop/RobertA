package com.app.raassoc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by New android on 09-11-2018.
 */

public class DocumentLibrary extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);
        ImageView vendRepr = (ImageView) findViewById(R.id.vendRepr);
        vendRepr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentLibrary.this, RepairsMain.class);
                startActivity(intent);
            }
        });

    }
}
