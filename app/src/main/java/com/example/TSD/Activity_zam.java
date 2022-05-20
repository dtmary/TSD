package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_zam extends AppCompatActivity {

    private static final String attrpki = "attrpki";
    private static final String attrcell = "attrcell";
    private static final String attrcnt = "attrcntzam";
    private static final String attrname = "attrname";
    private Activity activity = this;
    private ArrayList<Map<String, Object>> data;
    private AnoQuery qZam;
    private Handler prochandler;
    private String pki;
    private String sklad;
    private ListView ltRoot;
    private Handler handler;

    private String from[] = {attrpki, attrcell, attrcnt};
    private int to[] = {R.id.pki,R.id.cell ,R.id.cnt};

    private class SAdapter extends SimpleAdapter {

        public SAdapter(Activity_zam activity_zam, ArrayList<Map<String, Object>> data, int zamrow, String[] from, int[] to) {
            super(activity, data, R.layout.zamrow, from, to);
        }
    }

    private Activity_zam.SAdapter sAdapter;

    void drawlist() {
        try {
            sAdapter = new Activity_zam.SAdapter(this, data, R.layout.zamrow, from, to);
            ltRoot.setAdapter(sAdapter);
            ltRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Map<String, Object> m = (HashMap)data.get(position);
                    Intent intent = new Intent();
                    intent.putExtra("pkizam", (String)m.get(attrpki));
                    intent.putExtra("cellzam", (String)m.get(attrcell));
                    intent.putExtra("namezam", (String)m.get(attrname));
                    intent.putExtra("cntzam", (String)m.get(attrcnt));
                    setResult(RESULT_OK, intent);
                    activity.finish();
                }
            });
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    private class RefreshThread extends Thread {
        RefreshThread() {
            super();
            start();
        }
        public void run() {
            try {
                qZam = new AnoQuery(activity, R.raw.qzam);
                qZam.setParamString("PKI",pki);
                qZam.setParamString("SKLAD",sklad);
                qZam.Open();
                data = new ArrayList<Map<String, Object>>(qZam.recordcount());
                Map<String, Object> m;
                while (qZam.resultSet.next()) {
                    m = new HashMap<String, Object>();
                    m.put(attrpki,qZam.resultSet.getString(1));
                    m.put(attrcnt,qZam.resultSet.getString(2));
                    m.put(attrcell,qZam.resultSet.getString(3));
                    m.put(attrname,qZam.resultSet.getString(4));
                    data.add(m);
                }
            }    catch (Exception throwables) {
                throwables.printStackTrace();
            }
            handler.sendMessage(new Message());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zam);

        Bundle arguments = getIntent().getExtras();
        pki = arguments.get("pki").toString();
        sklad = arguments.get("sklad").toString();

        ltRoot = findViewById(R.id.ltRoot);

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                drawlist();
            }
        };

        RefreshThread refreshThread = new RefreshThread();
    }


    public void onSubmitClick(View view)
    {
        try {

        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }
}