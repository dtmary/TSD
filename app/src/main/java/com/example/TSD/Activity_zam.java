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

    private Activity activity = this;
    private AnoQuery qZam;
    private Handler prochandler;
    private String pki;
    private String sklad;
    private ListView ltRoot;

    private String from[] = {"PKI", "CELL", "CNT"};
    private int to[] = {R.id.pki,R.id.cell ,R.id.cnt};

    private class SAdapter extends SimpleAdapter {

        public SAdapter(Activity_zam activity_zam, ArrayList<Map<String, Object>> data, int zamrow, String[] from, int[] to) {
            super(activity, data, R.layout.zamrow, from, to);
        }
    }

    private Activity_zam.SAdapter sAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zam);

        Bundle arguments = getIntent().getExtras();
        pki = arguments.get("PKI").toString();
        sklad = arguments.get("SKLAD").toString();

        ltRoot = findViewById(R.id.ltRoot);

        ltRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Map<String, Object> m = (HashMap)qZam.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra("pkizam", (String)m.get("PKI"));
                intent.putExtra("cellzam", (String)m.get("CELL"));
                intent.putExtra("namezam", (String)m.get("NAMEPKI"));
                intent.putExtra("cntzam", (String)m.get("CNT"));
                setResult(RESULT_OK, intent);
                activity.finish();
            }
        });

        qZam = new AnoQuery(activity, R.raw.qzam,R.layout.zamrow,from,to,ltRoot);
        qZam.setParamString("PKI",pki);
        qZam.setParamString("SKLAD",sklad);
        qZam.Open();
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