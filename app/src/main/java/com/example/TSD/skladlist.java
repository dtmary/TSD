package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class skladlist extends AppCompatActivity {

    private String attrsklad = "attrbatch";
    private String attrskladname = "skladname";
    String[] from = {attrsklad,attrskladname};
    int[] to = {R.id.sklad, R.id.skladname};

    private ListView lskladlist;
    private Activity activity = this;

    private Handler handler;
    ArrayList<Map<String, Object>> data;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skladlist);
        lskladlist = (ListView)findViewById(R.id.lsRoot);

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                drawlist();
            }
        };
        skladlist.RefreshThread refreshThread = new skladlist.RefreshThread();

        lskladlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(activity, TrebList.class);
                Map<String, Object> m = (HashMap)data.get(position);
                String s = (String)m.get(attrsklad);
                intent.putExtra("sklad",s);
                startActivity(intent);
            }
        });

    }

    void refreshlist() {
        try {
            //Тест
            AnoQuery qSaveRsx = new AnoQuery(activity, R.raw.qtest);
            StringBuilder s = new StringBuilder();
            s.append("cntrec := cntrec + 1;");
            s.append("insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd) values ('000002', 15, '11111', '11111', 2, '11111', null, null);");
            s.append("cntrec := cntrec + 1;");
            qSaveRsx.setMacro("macro1",s.toString());
            qSaveRsx.Open();
            //Не тест


            AnoQuery qSkladlist = new AnoQuery(activity, R.raw.qsklad);
            qSkladlist.Open();
            data = new ArrayList<Map<String, Object>>(qSkladlist.recordcount());
            Map<String, Object> m;
            while (qSkladlist.resultSet.next()) {
                m = new HashMap<String, Object>();
                m.put(attrsklad,qSkladlist.resultSet.getString(1));
                m.put(attrskladname,qSkladlist.resultSet.getString(2));
                data.add(m);
            }
            handler.sendMessage(new Message());
        }    catch (Exception throwables) {
            throwables.printStackTrace();
        }

    }

    void drawlist() {
        try {
            SimpleAdapter sAdapter = new SimpleAdapter(this, data, R.layout.skladraw, from, to);
            lskladlist.setAdapter(sAdapter);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }
}