package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class rsx extends AppCompatActivity {

    private String batch;
    private String sklad;
    private String attrpki = "attrpki";
    private String attrnamepki = "attrnamepki";
    private String attrtreb = "attrtreb";
    private String attrotp = "attrotp";
    private String attrheadizd = "headizd";

    private String from[] = {attrnamepki,attrtreb,attrotp,attrheadizd};
    private int to[] = {R.id.pki,R.id.treb,R.id.otp,R.id.headizd};

    private Activity activity = this;
    private Handler handler;
    private ListView ltRoot;
    private SimpleAdapter sAdapter;
    private String scanCode = "";
    private EditText tScan;
    private SoundPool mSoundPool;
    private int soundIdbad;
    private int curPos;
    ArrayList<Map<String, Object>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsx);
        ltRoot = findViewById(R.id.ltRoot);
        tScan = findViewById(R.id.tScan);
        tScan.setFocusableInTouchMode(false);
        Bundle arguments = getIntent().getExtras();
        batch = arguments.get("batch").toString();
        sklad = arguments.get("sklad").toString();
        TextView txtBatch = (TextView)findViewById(R.id.txtBatch);
        txtBatch.setText(batch);

        mSoundPool = new SoundPool.Builder().build();
        soundIdbad = mSoundPool.load(this, R.raw.bad01, 1);

        tScan.setOnEditorActionListener(edtPKIOnEditorActionListener);

        handler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                drawlist();
            }
        };
        RefreshThread refreshThread = new RefreshThread();
    }

    private class RefreshThread extends Thread {
        RefreshThread() {
            super();
            start();
        }
        public void run() {
            try {
                AnoQuery qrsx = new AnoQuery(activity, R.raw.qrsx);
                qrsx.setParamString("sklad",sklad);
                qrsx.setParamString("batch",batch);
                qrsx.setParamString("company_id","1");
                qrsx.Open();
                data = new ArrayList<Map<String, Object>>(qrsx.recordcount());
                Map<String, Object> m;
                while (qrsx.resultSet.next()) {
                    m = new HashMap<String, Object>();
                    m.put(attrpki,qrsx.resultSet.getString(4));
                    m.put(attrnamepki,qrsx.resultSet.getString(4).concat(" - ").concat(qrsx.resultSet.getString(5)));
                    m.put(attrtreb,qrsx.resultSet.getString(9));
                    m.put(attrotp,"");
                    m.put(attrheadizd,qrsx.resultSet.getString(3));
                    data.add(m);
                }
            }    catch (Exception throwables) {
                throwables.printStackTrace();
            }
            handler.sendMessage(new Message());
        }
    }

    void drawlist() {
        try {
            sAdapter = new SimpleAdapter(this, data, R.layout.sostrow, from, to);
            ltRoot.setAdapter(sAdapter);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        tScan.setText("");
        tScan.setFocusableInTouchMode(true);
        tScan.requestFocus();
        tScan.setSelection(0);
        super.onKeyDown(keyCode, event);
        return true;
    }

    TextView.OnEditorActionListener edtPKIOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if(keyEvent.getAction() == KeyEvent.ACTION_UP && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                try {
                    tScan.setFocusableInTouchMode(false);
                    //Проверка на наличие
                    for (int idx = 0; idx < data.size(); idx++) {
                        Map<String, Object> m = (HashMap)data.get(idx);
                        String s = (String)m.get(attrpki);
                        String s1 = tScan.getText().toString();
                        if (s.equals(s1)) {
                            curPos = idx;
                        }
                    }
                    if (curPos == 0) {
                        mSoundPool.play(soundIdbad, 1, 1, 1, 0, 1f);
                    }
                    else {
                        Intent intent = new Intent(activity, cnt.class);
                        Map<String, Object> m = (HashMap)data.get(curPos);
                        intent.putExtra("namepki",(String)m.get(attrpki));
                        intent.putExtra("treb",(String)m.get(attrtreb));
                        intent.putExtra("otp",(String)m.get(attrotp));
                        startActivityForResult(intent,1);
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
        if (intent == null) {
            return;
        }
        String otp = intent.getStringExtra("otp");
        Map<String, Object> m = (HashMap)data.get(curPos);
        m.put(attrotp,otp);
        data.set(curPos,m);
        drawlist();
    }
}