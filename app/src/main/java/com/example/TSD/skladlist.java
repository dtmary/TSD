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
    private static final int MES_INSTALL_UPDATE = 3;
    private static final int REQ_UPDATE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String[] from = {"SKLAD","NAIM"};
    int[] to = {R.id.sklad, R.id.skladname};

    public ListView lskladlist;
    public Activity activity = this;

    private Handler handler;

    public AnoQuery qSkladlist;
    public SimpleAdapter sAdapter;

    File outputFile; //Файл apk для обновления программы

  /*  private class RefreshThread extends Thread {
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
    */


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
        //Обновление программы
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

        //Intent testintent = new Intent(activity, test.class);
        //startActivity(testintent);

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case (MES_INSTALL_UPDATE):
                        try {
                            Uri uri = FileProvider.getUriForFile(activity.getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", outputFile);
                            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
                            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(installIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    break;
                }
            }
        };
        Intent intent = new Intent(activity, UpdateActivity.class);
        startActivityForResult(intent,REQ_UPDATE);
       // qSkladlist = new AnoQuery(activity, R.raw.qsklad,R.layout.skladraw,from,to,lskladlist);
       // qSkladlist.Open();
    }


    public void Update(String apkurl) {

        try {
            String user = "mitya";
            String pass ="vjyrehfrr";
            String inpath=apkurl;
            NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("ELMEH",user, pass);
            SmbFile smbDir = new SmbFile(inpath,auth);
            SmbFile updateFile;
            updateFile = smbDir.listFiles()[0];
            for (SmbFile curFile : smbDir.listFiles()) {
                if (updateFile.createTime()<curFile.createTime()) {
                    updateFile = curFile;
                }
            }

            long installed = activity.getPackageManager().getPackageInfo( activity.getPackageName(), 0 ).lastUpdateTime;

            if (installed<updateFile.createTime())  {
                String outpath = Environment.getExternalStorageDirectory().getPath() + "/download/";
                File file = new File(outpath);
                file.mkdirs();
                outputFile = new File(file, "app.apk");

                SmbFileInputStream is = new SmbFileInputStream(updateFile);
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
                message.what = MES_INSTALL_UPDATE;
                handler.sendMessage(message);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode==REQ_UPDATE) {
            if (requestCode==RESULT_OK) {
                activity.finish();
            } else {
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

    }

}