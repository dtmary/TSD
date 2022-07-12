package com.example.TSD;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication111.BuildConfig;
import com.example.myapplication111.R;

import java.io.File;
import java.io.FileOutputStream;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

public class UpdateActivity extends AppCompatActivity {

    private static final int MES_CHECK_UPDATE = 2;
    private static final int MES_NOT_NEED_UPDATE = 1;
    private static final int MES_DOWNLOAD_UPDATE = 3;
    private static final int MES_UPDATE_COMPLETE = 4;

    private File outputFile; //Файл apk для обновления программы

    private Activity activity = this;
    private Handler handler;
    private TextView txtUpdate;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        verifyStoragePermissions(this);

        txtUpdate = findViewById(R.id.txtUpdate);
        Intent intent = new Intent();
        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case (MES_CHECK_UPDATE):
                        txtUpdate.setText("Проверка обновлений...");
                        break;
                    case (MES_NOT_NEED_UPDATE):
                        setResult(RESULT_CANCELED, intent);
                        activity.finish();
                        break;
                    case (MES_DOWNLOAD_UPDATE):
                        txtUpdate.setText("Загрузка обновлений...");
                        break;
                    case (MES_UPDATE_COMPLETE):
                        try {
                            txtUpdate.setText("Установка обновлений...");
                            Uri uri = FileProvider.getUriForFile(activity.getBaseContext(), BuildConfig.APPLICATION_ID + ".provider", outputFile);
                            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                            installIntent.setDataAndType(uri,"application/vnd.android.package-archive");
                            installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(installIntent);
                            setResult(RESULT_OK, intent);
                            activity.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }

            }
        };

        UpdateThread updatethread = new UpdateThread();
    }

    class UpdateThread extends Thread {
        UpdateThread() {
            super();
            start();
        }
        public void run() {
            try {
                Message message = new Message();
                message.what = MES_CHECK_UPDATE;
                handler.sendMessage(message);

                String user = "mitya";
                String pass ="vjyrehfrr";
                String inpath=getString(R.string.updateurl);
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
                    message = new Message();
                    message.what = MES_DOWNLOAD_UPDATE;
                    handler.sendMessage(message);
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
                    message = new Message();
                    message.what = MES_UPDATE_COMPLETE;
                    handler.sendMessage(message);
                } else {
                    message = new Message();
                    message.what = MES_NOT_NEED_UPDATE;
                    handler.sendMessage(message);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}