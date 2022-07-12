package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.BuildConfig;
import com.example.myapplication111.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jcifs.smb.*;

public class skladlist extends AppCompatActivity {

    private static final int MES_DRAW_LIST = 1;
    private static final int MES_NEED_UPDATE = 2;




    String[] from = {"SKLAD","NAIM"};
    int[] to = {R.id.sklad, R.id.skladname};

    public ListView lskladlist;
    public Activity activity = this;


    public AnoQuery qSkladlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skladlist);
        lskladlist = (ListView)findViewById(R.id.lsRoot);

        qSkladlist = new AnoQuery(activity, R.raw.qsklad,R.layout.skladraw,from,to,lskladlist);
        qSkladlist.Open();

        lskladlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(activity, TrebList.class);
                Map<String, Object> m = qSkladlist.getData().get(position);
                String s = (String)m.get("SKLAD");
                intent.putExtra("SKLAD",s);
                startActivity(intent);
            }
        });


    }



}