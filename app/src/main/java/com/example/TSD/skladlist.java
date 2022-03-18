package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
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
        Update(getString(R.string.updateurl));
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

    public void Update(String apkurl) {
        try {
            //TODO: в поток
            URL url = new URL(apkurl);
            URLConnection c = (URLConnection) url.openConnection();
            //c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();

            String PATH = Environment.getExternalStorageDirectory() + "/download/";
            File file = new File(PATH);
            file.mkdirs();
            File outputFile = new File(file, "app.apk");
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();//till here, it works fine - .apk is download to my sdcard in download file

            Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(PATH + "app.apk"))
                    .setType("application/android.com.app");
            startActivity(promptInstall);//installation is not working

        } catch (IOException e) {

            e.printStackTrace();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "channelID")
                            .setSmallIcon(R.drawable.ic_saved)
                            .setContentTitle("Update error!")
                            .setContentText("")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        } catch (Exception e) {

            e.printStackTrace();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getApplicationContext(), "channelID")
                            .setSmallIcon(R.drawable.ic_saved)
                            .setContentTitle("Update error!")
                            .setContentText("")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        }

    }

}