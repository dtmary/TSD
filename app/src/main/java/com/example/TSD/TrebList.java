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
import com.example.myapplication111.R;
import com.example.myapplication111.databinding.ActivityMainBinding;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.myapplication111.R.string.streblisthead;


public class TrebList extends AppCompatActivity {

  static String LOG_TAG = "mainlog";
  String attrbatch = "attrbatch";
  String attrspz = "attrspz";
  String attrcreatedate = "attrcreatedate";
  String attrpkiinfo = "attrpkiinfo";
  String sklad;
  String[] from = {attrbatch, attrspz, attrcreatedate,attrpkiinfo};
  int[] to = {R.id.batch,R.id.spz,R.id.createdate,R.id.pkiinfo};

    private class RefreshThread extends Thread {
        RefreshThread() {
            super();
            start();
        }
        public void run() {
            refreshlist();
            handler.sendMessage(new Message());
        }
    }

    private Activity activity = this;
    private AnoQuery qTreblist;
    private Handler handler;
    ArrayList<Map<String, Object>> data;

    ListView ltreblistroot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treb_list);

        Bundle arguments = getIntent().getExtras();
        sklad= arguments.get("sklad").toString();
        TextView txtBatch = (TextView)findViewById(R.id.treblisthead);
        txtBatch.setText(sklad.concat(getString(streblisthead)));

        ltreblistroot = (ListView)findViewById(R.id.ltRoot);

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                drawlist();
            }
        };

        RefreshThread refreshThread = new RefreshThread();

        ltreblistroot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(activity, rsx.class);
                Map<String, Object> m = (HashMap)data.get(position);
                String s = (String)m.get(attrbatch);
                intent.putExtra("batch",s);
                startActivity(intent);
            }
        });


    }

    void refreshlist() {
        try {
            qTreblist = new AnoQuery(activity, R.raw.qtreblist);
            qTreblist.setParamString("sklad",sklad);
            qTreblist.Open();
            data = new ArrayList<Map<String, Object>>(qTreblist.recordcount());
            Map<String, Object> m;
            while (qTreblist.resultSet.next()) {
                m = new HashMap<String, Object>();
                m.put(attrbatch,qTreblist.resultSet.getString(1));
                m.put(attrspz,qTreblist.resultSet.getString(2));
                m.put(attrcreatedate,qTreblist.resultSet.getString(3));
                m.put(attrpkiinfo,qTreblist.resultSet.getString(4));
                data.add(m);
            }
            handler.sendMessage(new Message());
        }    catch (Exception throwables) {
            throwables.printStackTrace();
        }

    }

    void drawlist() {
        try {
            SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.trebraw, from, to);
            ltreblistroot.setAdapter(sAdapter);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}