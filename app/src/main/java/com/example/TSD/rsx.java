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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

import java.text.DecimalFormat;
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

    private String from[] = {"NAMEPKI","CELL", "TREB","OST","OTP","HEADIZD"};
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
    private AnoQuery qrsx;

    void UpdateHighlite(int position) {
        try {
            HashMap rec = (HashMap) qrsx.getData().get(position);
            int iTreb = Integer.valueOf((String) rec.get("TREB"));
            int iOtp =  Integer.valueOf((String) rec.get("OTP"));
            int iOst =  Integer.valueOf((String) rec.get("OST"));
            if (iOst == 0 || iOst < iOtp) {
                qrsx.setColor(position, R.color.WhiteRed); //Не хватает остатков
            } else if (iTreb == iOtp) {
                qrsx.setColor(position, R.color.WhiteGreen); //Полностью отпущено
            } else {
                qrsx.setColor(position, R.color.white);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

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
        prochandler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {finishSave(msg.what);}
        };

        qrsx = new AnoQuery(activity, R.raw.qrsx);
        qrsx = new AnoQuery(activity, R.raw.qrsx,R.layout.sostrow,from,to,ltRoot);
        qrsx.setParamString("sklad",skladin);
        qrsx.setParamString("batch",batch);
        qrsx.setParamString("company_id","1");
        qrsx.Open();

        ltRoot.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                try {
                    qrsx.adapter.offset = ltRoot.getFirstVisiblePosition();
                } catch (Throwable e) {};
            }
        });

        ltRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                qrsx.deselect();
                qrsx.select(position);
            }
        });
    }

    private void finishSave(int resultcode) {
        try {
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
                intent.putExtra("message", qSaveRsx.GetResultMessage());
                startActivityForResult(intent, REQ_MESSAGE);
            } else {
                Intent intent = new Intent(activity, message.class);
                intent.putExtra("message", qSaveRsx.GetResultMessage());
                startActivityForResult(intent, ERR_MESSAGE);
            }
        }catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rsx_menu, menu);
        return true;
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
                    for (int idx = 0; idx < qrsx.getData().size(); idx++) {
                        Map<String, Object> m = (HashMap)qrsx.getData().get(idx);
                        String s = (String)m.get("PKI");
                        String s1 = tScan.getText().toString();
                        if (s.equals(s1)) {
                            curPos = idx;
                        }
                    }
                    if (curPos == -1) {
                        mSoundPool.play(soundIdbad, 1, 1, 1, 0, 1f);
                    }
                    else {
                        qrsx.select(curPos);
                        Intent intent = new Intent(activity, cnt.class);
                        Map<String, Object> m = (HashMap)qrsx.getData().get(qrsx.selected());
                        intent.putExtra("namepki",(String)m.get("PKI"));
                        intent.putExtra("treb",(String)m.get("TREB"));
                        intent.putExtra("otp",(String)m.get("OTP"));
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
                qrsx.setSelectedFieldValue("OTP",intent.getStringExtra("otp"));
                UpdateHighlite(qrsx.selected());
                qrsx.drawgrid();
            }
            if (requestCode == REQ_MESSAGE) {
                setResult(RESULT_OK, intent);
                activity.finish();
            }
            if (requestCode == REQ_ZAM) {
                Map<String, Object> m = (HashMap) qrsx.getData().get(qrsx.selected());
                String pkizam = intent.getStringExtra("pkizam");
                String cellzam = intent.getStringExtra("cellzam");
                String namezam = intent.getStringExtra("namezam");
                String ostzam = intent.getStringExtra("ostzam");
                float cntzam = Float.valueOf(intent.getStringExtra("cntzam"));
                float cnttreb = Float.parseFloat((String)m.get("TREB"));

                //сохранение данных по позиции состава
                m.put("OLDPKI", m.get("PKI"));
                m.put("OLDTREB", m.get("TREB"));
                m.put("OLDCELL", m.get("CELL"));
                m.put("OLDNAME",m.get("NAMEPKI"));
                m.put("OLDOST", m.get("OST"));

                //добавление замены
                m.put("PKI", pkizam);
                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                m.put("TREB", decimalFormat.format(cnttreb*cntzam));
                m.put("CELL", cellzam);
                m.put("NAMEPKI",namezam);
                m.put("OST",ostzam);
                qrsx.getData().set(qrsx.selected(), m);
                UpdateHighlite(qrsx.selected());
                qrsx.drawgrid();
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
            Map<String, Object> m = (HashMap) qrsx.getData().get(qrsx.selected());
            intent.putExtra("PKI", (String) m.get("PKI"));
            intent.putExtra("SKLAD", skladin);
            startActivityForResult(intent, REQ_ZAM);
        }
        //Возврат замены
        if (item.getItemId()==R.id.act_notzam) {
            Map<String, Object> m = (HashMap) qrsx.getData().get(qrsx.selected());
            if (m.get("OLDPKI")!=null) {
                m.put("PKI", m.get("OLDPKI"));
                m.put("TREB", m.get("OLDTREB"));
                m.put("CELL", m.get("OLDCELL"));
                m.put("NAMEPKI", m.get("OLDNAME"));
                m.put("OST",m.get("OLDOST"));
                m.put("OLDPKI", null);
                qrsx.getData().set(qrsx.selected(), m);
                UpdateHighlite(qrsx.selected());
                qrsx.drawgrid();
            }
         }

        if (item.getItemId()==R.id.act_cnt) {
            Intent intent = new Intent(activity, cnt.class);
            Map<String, Object> m = (HashMap)qrsx.getData().get(qrsx.selected());
            intent.putExtra("namepki",(String)m.get("PKI"));
            intent.putExtra("treb",(String)m.get("TREB"));
            intent.putExtra("otp",(String)m.get("OTP"));
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
            for (int idx = 0; idx < qrsx.getData().size(); idx++) {
                Map<String, Object> m = (HashMap) qrsx.getData().get(idx);
                String pki = (String) m.get("PKI");
                String itc = (String) m.get("OTP");
                String mgnbr = (String) m.get("DOCNUM");
                String mglot = (String) m.get("INDNUM");
                String spz = (String) m.get("SHPZ");

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
            for (int idx = 0; idx < qrsx.getData().size(); idx++) {
                Map<String, Object> m = (HashMap) qrsx.getData().get(idx);
                float fitc = 0;
                float fost = 0;
                String pki   = (String) m.get("PKI");
                String stitc = (String) m.get("OTP");
                String stost = (String) m.get("OST");
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