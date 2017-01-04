package com.example.optisol2.camera5;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Nextpage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lastscreen);
        Intent in = getIntent();
        String path = in.getStringExtra("path");
        ImageView i = (ImageView) findViewById(R.id.imageView1);
        Bitmap new_image = BitmapFactory.decodeFile(path);
        i.setImageBitmap(new_image);
        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });

    }

}