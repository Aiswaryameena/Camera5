package com.example.optisol2.camera5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by optisol2 on 21-12-2016.
 */
public class ShareImages extends Activity implements AdapterView.OnItemSelectedListener{
    ListView lv;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_display);
        lv=(ListView)findViewById(R.id.list);
        String[] values={"Bluetooth","whatsapp","shareit","hike","facebook"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,values);
        lv.setAdapter(arrayAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Toast.makeText(getApplicationContext(),"Redirecting...",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
//donothing
    }
}
