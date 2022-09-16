package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.TSD.AnO.AnoQuery;
import com.example.TSD.AnO.AnoStoredProc;
import com.example.mApp;
import com.example.myapplication111.R;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication111.R.string.streblisthead;


public class TrebList extends AppCompatActivity {

    private static final int REQ_NEXT = 1;

  static String LOG_TAG = "mainlog";
  String sklad;
  String[] from = {"BATCH",  "CREATEDATE","PKIINFO"};
  int[] to = {R.id.batch,R.id.createdate,R.id.pkiinfo};


    private Activity activity = this;
    private AnoQuery qTreblist;

    ListView ltreblistroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treb_list);
        setTitle("Кладовая ОВК - ".concat(mApp.userId));

        Bundle arguments = getIntent().getExtras();
        sklad= arguments.get("SKLAD").toString();
        TextView txtBatch = (TextView)findViewById(R.id.treblisthead);
        txtBatch.setText(sklad.concat(getString(streblisthead)));
        ltreblistroot = (ListView)findViewById(R.id.ltRoot);
        ltreblistroot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(activity, docheader.class);
                intent.putExtra("BATCH",qTreblist.getString(position,"BATCH"));
                intent.putExtra("SKLAD",sklad);
                startActivityForResult(intent,REQ_NEXT);
            }
        });

        qTreblist = new AnoQuery(activity, R.raw.qtreblist,R.layout.trebraw,from,to,ltreblistroot);
        qTreblist.setParamString("SKLAD",sklad);
        qTreblist.Open();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==REQ_NEXT) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }


}