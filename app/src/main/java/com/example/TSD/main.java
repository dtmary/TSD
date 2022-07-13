package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.TSD.AnO.AnoQuery;
import com.example.mApp;
import com.example.myapplication111.R;

import java.io.File;


public class main extends AppCompatActivity {

    private Handler handler;

    private static final int REQ_UPDATE = 1;
    private static int REQ_CREATEDOC = 2;
    private static final int MES_INSTALL_UPDATE = 3;
    private static int ERR_MESSAGE = 4;

    private Activity activity = this;
    private EditText edtTabn;
    private Handler qhandler;
    AnoQuery qUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(activity, UpdateActivity.class);
        startActivityForResult(intent,REQ_UPDATE);

        edtTabn = findViewById(R.id.edtTabn);
        edtTabn.setFocusableInTouchMode(false);
        edtTabn.setOnEditorActionListener(edtTabnOnEditorActionListener);
        mApp.mSoundPool = new SoundPool.Builder().build();
        mApp.soundIdbad = mApp.mSoundPool.load(this, R.raw.bad01, 1);

        qhandler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                if (qUser.getData().size()==0) {
                    mApp.mSoundPool.play(mApp.soundIdbad, 1, 1, 1, 0, 1f);
                    Intent intent = new Intent(activity, message.class);
                    intent.putExtra("message", "Пользователь не найден!");
                    startActivityForResult(intent, ERR_MESSAGE);
                } else {
                    mApp.tabn = edtTabn.getText().toString();
                    mApp.userId = (String) qUser.getData().get(0).get("LOGIN");
                    Intent intent = new Intent(activity, skladlist.class);
                    startActivityForResult(intent, REQ_CREATEDOC);
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        edtTabn.setText("");
        edtTabn.setFocusableInTouchMode(true);
        edtTabn.requestFocus();
        edtTabn.setSelection(0);
        super.onKeyDown(keyCode, event);
        return true;
    }

    TextView.OnEditorActionListener edtTabnOnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                try {
                    edtTabn.setFocusableInTouchMode(false);
                    String qr = edtTabn.getText().toString();
                    String pref = qr.substring(0,4);
                    if (pref.equals("TABN")) {
                        String tabn = qr.substring(4);
                        qUser = new AnoQuery(activity, R.raw.quser, qhandler);
                        qUser.setParamString("TABN",tabn);
                        qUser.Open();
                    } else {
                        mApp.mSoundPool.play(mApp.soundIdbad, 1, 1, 1, 0, 1f);
                    }
                }
                catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

            if (requestCode == REQ_CREATEDOC) {
                mApp.userId = "";
                mApp.tabn = "";
            }

    }
}