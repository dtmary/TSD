package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.MenuCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    private static final int REQ_CNT = 1;
    private static final int REQ_MESSAGE = 2;
    private static final int ERR_MESSAGE = 3;
    private static final int REQ_ZAM = 4;
    private static final int REQ_CLOSEWIND = 5;
    private String batch;
    private String skladin;
    private String skladout;
    private String folder;
    private String attrpki = "attrpki";
    private String attrnamepki = "attrnamepki";
    private String attrtreb = "attrtreb";
    private String attrotp = "attrotp";
    private String attrheadizd = "headizd";
    private String attrcell = "attrcell";
    private String attrost = "attrost";
    private String attrshpz = "attrshpz";
    private String attrmgnbr = "attrmgnbr";
    private String attrmglot = "attrmglot";
    private String attroldpki = "attroldpki";
    private String attroldtreb = "attroldtreb";
    private String attroldcell = "attroldcell";
    private String attroldname = "attroldname";

    private String from[] = {attrnamepki,attrcell, attrtreb,attrost,attrotp,attrheadizd};
    private int to[] = {R.id.pki,R.id.cell ,R.id.treb,R.id.ost,R.id.otp,R.id.headizd};

    private Activity activity = this;
    private Handler handler;
    private Handler prochandler;
    private ListView ltRoot;
    private String scanCode = "";
    private EditText tScan;
    private SoundPool mSoundPool;
    private int soundIdbad;
    private int curPos;
    private  Thread t;
    private AnoQuery qSaveRsx;
    private String selectedPki;
    private int selectedPosition = -1;
    ArrayList<Map<String, Object>> data;

    private class SAdapter extends SimpleAdapter {
        public SAdapter(rsx rsx, ArrayList<Map<String, Object>> data, int sostrow, String[] from, int[] to) {
            super(activity, data, R.layout.sostrow, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (position == selectedPosition) {
                view.setBackgroundResource(R.color.Selected);
            }
            else {
                view.setBackgroundResource(R.color.white);
                HashMap rec = (HashMap) data.get(position);
                String sTreb = (String) rec.get(attrtreb);
                String sOtp = (String) rec.get(attrotp);
                Float treb = Float.valueOf(sTreb);
                Float otp = 0.f;
                if (!sOtp.equals("")) {
                    otp = Float.valueOf(sOtp);
                }
                if (treb.equals(otp)) {
                    view.setBackgroundResource(R.color.WhiteGreen);
                }
                float fOst = 0;
                if (!((String) rec.get(attrost)).equals("")) {
                    fOst = Float.parseFloat((String) rec.get(attrost));
                }
                if (otp > fOst) {
                    view.setBackgroundResource(R.color.WhiteRed);
                }
            }
            return view;
        }
    }

    private SAdapter sAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsx);
        ltRoot = findViewById(R.id.ltRoot);
        tScan = findViewById(R.id.tScan);
        tScan.setFocusableInTouchMode(false);
        Bundle arguments = getIntent().getExtras();
        batch = arguments.get("batch").toString();
        skladin = arguments.get("skladin").toString();
        skladout = arguments.get("skladout").toString();
        folder = arguments.get("folder").toString();
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

        prochandler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {finishSave(msg.what);}
        };
    }

    private void finishSave(int resultcode) {
        if (resultcode == 20999) {
            Context context = activity.getApplicationContext();
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context, "channelID")
                            .setSmallIcon(R.drawable.ic_saved)
                            .setContentTitle(qSaveRsx.GetResultMessage())
                            .setContentText("")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            try {
                notificationManager.notify(101, builder.build());
            } catch (Exception throwables) {
                throwables.printStackTrace();
            }

            Intent intent = new Intent(activity, message.class);
            intent.putExtra("message",qSaveRsx.GetResultMessage());
            startActivityForResult(intent,REQ_MESSAGE);
        }
        else {
            Intent intent = new Intent(activity, message.class);
            intent.putExtra("message",qSaveRsx.GetResultMessage());
            startActivityForResult(intent,ERR_MESSAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rsx_menu, menu);
        //menu.setGroupDividerEnabled(true);
        return true;
    }

    private class RefreshThread extends Thread {
        RefreshThread() {
            super();
            start();
        }
        public void run() {
            try {
                AnoQuery qrsx = new AnoQuery(activity, R.raw.qrsx);
                qrsx.setParamString("sklad",skladin);
                qrsx.setParamString("batch",batch);
                qrsx.setParamString("company_id","1");
                qrsx.Open();
                data = new ArrayList<Map<String, Object>>(qrsx.recordcount());
                Map<String, Object> m;
                while (qrsx.resultSet.next()) {
                    m = new HashMap<String, Object>();
                    m.put(attrmgnbr,qrsx.resultSet.getString(1));
                    m.put(attrmglot,qrsx.resultSet.getString(2));
                    m.put(attrpki,qrsx.resultSet.getString(4));
                    m.put(attrnamepki,qrsx.resultSet.getString(4).concat(" - ").concat(qrsx.resultSet.getString(5).substring(0,8)));
                    m.put(attrtreb,qrsx.resultSet.getString(9));
                    m.put(attrotp,"");
                    m.put(attrheadizd,qrsx.resultSet.getString(3));
                    m.put(attrcell,qrsx.resultSet.getString(11));
                    m.put(attrost,qrsx.resultSet.getString(12));
                    m.put(attrshpz,qrsx.resultSet.getString(10));
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
            sAdapter = new SAdapter(this, data, R.layout.sostrow, from, to);
            ltRoot.setAdapter(sAdapter);
            ltRoot.setSelection(selectedPosition);
            ltRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                     try {
                    Map<String, Object> m = (HashMap) data.get(position);
                    selectedPki = (String) m.get(attrpki);
                    View sView = view;
                    if (selectedPosition == position) {
                        sView.setBackgroundResource(R.color.white);
                        selectedPosition = -1;
                    } else {
                        sView.setBackgroundResource(R.color.Selected);
                        if (selectedPosition != -1) {
                            View v = ltRoot.getChildAt(selectedPosition - ltRoot.getFirstVisiblePosition());
                            if (v != null) {
                                v.setBackgroundResource(R.color.white);
                            }
                        }
                        selectedPosition = position;
                    }
                } catch (Exception e)

                {
                    e.printStackTrace();
                }
                }
            });

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
                    curPos = -1;
                    for (int idx = 0; idx < data.size(); idx++) {
                        Map<String, Object> m = (HashMap)data.get(idx);
                        String s = (String)m.get(attrpki);
                        String s1 = tScan.getText().toString();
                        if (s.equals(s1)) {
                            curPos = idx;
                        }
                    }
                    if (curPos == -1) {
                        mSoundPool.play(soundIdbad, 1, 1, 1, 0, 1f);
                    }
                    else {
                        selectedPosition = curPos;
                        Intent intent = new Intent(activity, cnt.class);
                        Map<String, Object> m = (HashMap)data.get(selectedPosition);
                        intent.putExtra("namepki",(String)m.get(attrpki));
                        intent.putExtra("treb",(String)m.get(attrtreb));
                        intent.putExtra("otp",(String)m.get(attrotp));
                        startActivityForResult(intent,REQ_CNT);
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
        if (resultCode==RESULT_CANCELED) {
            return;
        }
        try {
            if (requestCode == REQ_CNT) {
                String otp = intent.getStringExtra("otp");
                Map<String, Object> m = (HashMap) data.get(selectedPosition);
                m.put(attrotp, otp);
                data.set(selectedPosition, m);
                drawlist();
                //ltRoot.smoothScrollToPosition(selectedPosition);//setSelection(selectedPosition);
            }
            if (requestCode == REQ_MESSAGE) {
                setResult(RESULT_OK, intent);
                activity.finish();
            }
            if (requestCode == REQ_ZAM) {
                Map<String, Object> m = (HashMap) data.get(selectedPosition);
                String pkizam = intent.getStringExtra("pkizam");
                String cellzam = intent.getStringExtra("cellzam");
                String namezam = intent.getStringExtra("namezam");
                float cntzam = Float.valueOf(intent.getStringExtra("cntzam"));
                float cnttreb = Float.parseFloat((String)m.get(attrtreb));

                //сохранение данных по позиции состава
                m.put(attroldpki, m.get(attrpki));
                m.put(attroldtreb, m.get(attrtreb));
                m.put(attroldcell, m.get(attrcell));
                m.put(attroldname,m.get(attrnamepki));

                //добавление замены
                m.put(attrpki, pkizam);
                m.put(attrtreb, Float.toString(cnttreb*cntzam));
                m.put(attrcell, cellzam);
                m.put(attrnamepki,pkizam.concat(" - ").concat(namezam));
                data.set(selectedPosition, m);
                drawlist();
            }
            if (requestCode == REQ_CLOSEWIND) {
                if (resultCode == RESULT_OK) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (item.getItemId() == R.id.act_rsx_save) {
            saveRsx();
        }
        //Показываем позиции замены
        if (item.getItemId() == R.id.act_zam) {
            Intent intent = new Intent(activity, Activity_zam.class);
            Map<String, Object> m = (HashMap) data.get(selectedPosition);
            intent.putExtra("pki", (String) m.get(attrpki));
            intent.putExtra("sklad", skladin);
            startActivityForResult(intent, REQ_ZAM);
        }
        //Возврат замены
        if (item.getItemId()==R.id.act_notzam) {
            Map<String, Object> m = (HashMap) data.get(selectedPosition);
            if (m.get(attroldpki)!=null) {
                m.put(attrpki, m.get(attroldpki));
                m.put(attrtreb, m.get(attroldtreb));
                m.put(attrcell, m.get(attroldcell));
                m.put(attrnamepki, m.get(attroldname));
                m.put(attroldpki, null);
                data.set(selectedPosition, m);
                drawlist();
            }
         }

        if (item.getItemId()==R.id.act_cnt) {
            Intent intent = new Intent(activity, cnt.class);
            Map<String, Object> m = (HashMap)data.get(selectedPosition);
            intent.putExtra("namepki",(String)m.get(attrpki));
            intent.putExtra("treb",(String)m.get(attrtreb));
            intent.putExtra("otp",(String)m.get(attrotp));
            startActivityForResult(intent,REQ_CNT);
        }
        return super.onOptionsItemSelected(item);
    }


    public void saveRsx() {

        StringBuilder errString = new StringBuilder();
        if (!testData(errString)) {
            Intent intent = new Intent(activity, message.class);
            intent.putExtra("message", errString.toString());
            startActivityForResult(intent, ERR_MESSAGE);
        }
        else {
            qSaveRsx = new AnoQuery(activity, R.raw.qsaversx, prochandler);
            StringBuilder sql = new StringBuilder();

            sql.append("v_skladin := '" + skladin + "';");
            sql.append("v_skladout := '" + skladout + "';");
            sql.append("v_folder := '" + folder + "';");
            for (int idx = 0; idx < data.size(); idx++) {
                Map<String, Object> m = (HashMap) data.get(idx);
                String pki = (String) m.get(attrpki);
                String itc = (String) m.get(attrotp);
                String mgnbr = (String) m.get(attrmgnbr);
                String mglot = (String) m.get(attrmglot);
                String spz = (String) m.get(attrshpz);

                if (!itc.equals("0") && !itc.equals("")) {
                    sql.append("insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd)values('" + pki + "', " + itc + ", '" + mgnbr + "', '" + mglot + "', " + idx + ", '" + spz + "', null, null);");
                    sql.append("cntrec := cntrec + 1;");
                }
            }

            qSaveRsx.setMacro("macroparams", sql.toString());
            qSaveRsx.Open();
        }
    }

    public boolean testData(StringBuilder s) {
        boolean result = true;

        try {
            for (int idx = 0; idx < data.size(); idx++) {
                Map<String, Object> m = (HashMap) data.get(idx);
                float fitc = 0;
                float fost = 0;
                String pki   = (String) m.get(attrpki);
                String stitc = (String) m.get(attrotp);
                String stost = (String) m.get(attrost);
                if (!stitc.equals("")) {
                    fitc = Float.parseFloat(stitc);
                }
                if (!stost.equals("")) {
                    fost = Float.parseFloat(stost);
                }
                if (fitc>fost) {
                    result = false;
                    s.append("ПКИ "+pki+": не хватает остатков\n");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            result = false;
            s.append("Неизвестная ошибка\n");
        }
        return(result);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(activity, messageyesno.class);
        intent.putExtra("message", "Прервать операцию?");
        startActivityForResult(intent,REQ_CLOSEWIND);
    }
}