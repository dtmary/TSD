package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
import android.widget.Toast;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.BuildConfig;
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

import jcifs.smb.*;

public class skladlist extends AppCompatActivity {

    private static final int MES_DRAW_LIST = 1;
    private static final int MES_NEED_UPDATE = 2;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String attrsklad = "attrbatch";
    private String attrskladname = "skladname";
    String[] from = {attrsklad,attrskladname};
    int[] to = {R.id.sklad, R.id.skladname};

    private ListView lskladlist;
    private Activity activity = this;

    private Handler handler;
    ArrayList<Map<String, Object>> data;

    File outputFile; //Файл apk для обновления программы

    private class RefreshThread extends Thread {
        RefreshThread() {
            super();
            start();
        }
        public void run() {
            refreshlist();
            Message message = new Message();
            message.what = MES_DRAW_LIST;
            handler.sendMessage(message);
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        verifyStoragePermissions(this);
        //Обновленпие программы
        class UpdateThread extends Thread {
            UpdateThread() {
                super();
                start();
            }
            public void run() {
                Update(getString(R.string.updateurl));
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skladlist);
        lskladlist = (ListView)findViewById(R.id.lsRoot);

        try {
            String outpath = Environment.getExternalStorageDirectory().getPath() + "/download/";
            File file = new File(outpath);
            file.mkdirs();
            outputFile = new File(file, "app.apk");
            Uri uri = FileProvider.getUriForFile(activity.getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", outputFile);
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
            //installIntent.setDataAndType(Uri.fromFile(outputFile),"application/vnd.android.package-archive");
            //installIntent.setData(uri);
            //installIntent.setType("application/vnd.android.package-archive");

            //installIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(installIntent);

        } catch  (Exception e) {
            e.printStackTrace();
        }
        // UpdateThread updatethread = new UpdateThread();

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case (MES_DRAW_LIST):
                        drawlist();
                        break;
                    case (MES_NEED_UPDATE):
                        try {
                           /* Intent promptInstall = new Intent(Intent.ACTION_VIEW)
                                    .setData(Uri.parse(outputFile + "app.apk"))
                                    .setType("application/android.com.app");
                            startActivity(promptInstall);//installation is not working
                            */
                            //Uri uri = Uri.fromFile(outputFile);

                          /*  Uri uri = FileProvider.getUriForFile(activity.getBaseContext(), BuildConfig.APPLICATION_ID + ".provider",outputFile);
                            Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            install.setDataAndType(uri, "application/vnd.android.package-archive");
                            //install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(install);*/

                            Uri uri = FileProvider.getUriForFile(activity.getBaseContext(), BuildConfig.APPLICATION_ID + ".provider",outputFile);
                            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            installIntent.setDataAndType(uri,
                                    "application/vnd.android.package-archive");
                            startActivity(installIntent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

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
            Message message = new Message();
            message.what = MES_DRAW_LIST;
            handler.sendMessage(message);
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
            String user = "mitya";
            String pass ="vjyrehfrr";
            String inpath=apkurl;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ELMEH",user, pass);
            SmbFile smbFile = new SmbFile(inpath,auth);
            SmbFileInputStream is = new SmbFileInputStream(smbFile);

            String outpath = Environment.getExternalStorageDirectory().getPath() + "/download/";
            File file = new File(outpath);
            file.mkdirs();
            outputFile = new File(file, "app.apk");
            FileOutputStream fos = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while (len1  != -1) {
                len1 = is.read(buffer);
                if (len1 != -1) {
                    fos.write(buffer, 0, len1);
                }
            }
            fos.close();
            is.close();

            Message message = new Message();
            message.what = MES_NEED_UPDATE;
            handler.sendMessage(message);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        /*
        try {
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
         */

    }

}