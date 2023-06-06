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
import com.example.mApp;
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
    private boolean zamall;

    private String from[] = {"PKI", "CELL", "CNTFORMAT"};
    private int to[] = {R.id.pki,R.id.cell ,R.id.cntformat};

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
        setTitle("Кладовая ОВК - ".concat(mApp.userId));

        Bundle arguments = getIntent().getExtras();
        pki = arguments.get("PKI").toString();
        sklad = arguments.get("SKLAD").toString();
        zamall = arguments.getBoolean("ZAMALL");

        ltRoot = findViewById(R.id.ltRoot);

        ltRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("pkizam", qZam.getString(position,"PKI"));
                intent.putExtra("cellzam", qZam.getString(position,"CELL"));
                intent.putExtra("namezam", qZam.getString(position,"NAMEPKI"));
                intent.putExtra("cntzam", qZam.getString(position,"CNTFULL"));
                intent.putExtra("ostzam",qZam.getString(position,"OST"));
                intent.putExtra("edzam",qZam.getString(position,"KOD_EI"));
                intent.putExtra("zamall",zamall);

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