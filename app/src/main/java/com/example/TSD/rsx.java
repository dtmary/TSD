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
import android.icu.number.Precision;
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

import com.example.TSD.AnO.AnoMath;
import com.example.TSD.AnO.AnoQuery;
import com.example.mApp;
import com.example.myapplication111.R;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class rsx extends AppCompatActivity {

    private static final int REQ_CNT = 1;
    private static final int REQ_MESSAGE = 2;
    private static final int ERR_MESSAGE = 3;
    private static final int REQ_ZAM = 4;
    private static final int REQ_CLOSEWIND = 5;
    private static final int REQ_CREATEDEFICIT = 6;
    private String batch;
    private String skladin;
    private String skladout;
    private String folder;
    private boolean operationstarted = false;

    private String from[] = {"NAMEPKI","CELL", "TREB","OST","OTP","HEADIZD"};
    private int to[] = {R.id.pki,R.id.cell ,R.id.treb,R.id.ost,R.id.otp,R.id.headizd};

    private Activity activity = this;
    private Handler prochandler;
    private ListView ltRoot;
    private String scanCode = "";
    private EditText tScan;
    private int curPos;
    private  Thread t;
    private AnoQuery qSaveRsx;
    private AnoQuery qrsx;
    private boolean deficit = false;
    private DecimalFormat dF;

    void UpdateHighlite(int position) {
        try {

            float fTreb = qrsx.getFloat(position,"TREB");
            float fOtp =  qrsx.getFloat(position,"OTP"); //Float.valueOf((String) rec.get("OTP"));
            float fOst =  qrsx.getFloat(position,"OST");
            if (fOst == 0 || fOst < fOtp) {
                qrsx.setColor(position, R.color.WhiteRed); //Не хватает остатков
            } else if (fTreb <= fOtp) {
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
        setTitle("Кладовая ОВК - ".concat(mApp.userId));
        dF = new DecimalFormat("#.###");
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

        tScan.setOnEditorActionListener(edtPKIOnEditorActionListener);
        prochandler = new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {finishSave(msg.what);}
        };

        //qrsx = new AnoQuery(activity, R.raw.qrsx);
        qrsx = new AnoQuery(activity, R.raw.qrsx,R.layout.sostrow,from,to,ltRoot);
        qrsx.setParamString("sklad",skladin);
        qrsx.setParamString("batch",batch);
        qrsx.setParamString("company_id","1");
        qrsx.sethBeforeDrawGrid(new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                for (int i = 0; i < qrsx.getData().size(); i++) {
                    UpdateHighlite(i);
                }
            }
        });
        qrsx.Open();


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
                        float ftitc = qrsx.getFloat(idx,"OTP");
                        float ftreb = qrsx.getFloat(idx,"TREB");
                        String s1 = tScan.getText().toString();
                        if (s1.equals(qrsx.getString(idx,"PKI"))&&(ftitc < ftreb)) {
                            curPos = idx;
                            break;
                        }
                    }
                    if (curPos == -1) {
                        mApp.mSoundPool.play(mApp.soundIdbad, 1, 1, 1, 0, 1f);
                    }
                    else {
                        qrsx.deselect();
                        qrsx.select(curPos);
                        Intent intent = new Intent(activity, cnt.class);
                        intent.putExtra("namepki",qrsx.getString("PKI"));
                        intent.putExtra("treb",qrsx.getString("TREBALL"));
                        intent.putExtra("otp",qrsx.getString("OTP"));
                        intent.putExtra("scan",true);
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
                float curost = Float.valueOf(intent.getStringExtra("otp"));
                float curotp;
                boolean scan = intent.getBooleanExtra("scan", false);
                String curpki = qrsx.getString("PKI");
                if (curost > 0) {operationstarted = true;}
                if (scan) {
                    for (int i = 0; i < qrsx.getFloat("CNTZAP"); i++) {
                        if (!curpki.equals(qrsx.getString(qrsx.selected() + i, "PKI"))) {
                            break;
                        }
                        ;
                        if (curost >= qrsx.getFloat(qrsx.selected() + i, "TREB")) {
                            curotp = qrsx.getFloat(qrsx.selected() + i, "TREB");
                        } else {
                            curotp = curost;
                        }
                        //На последнюю списываем все оставшиеся
                        if (i == qrsx.getFloat("CNTZAP") - 1) {
                            qrsx.setFloat(qrsx.selected() + i, "OTP", curost);
                        } else {
                            qrsx.setFloat(qrsx.selected() + i, "OTP", curotp);
                            curost = AnoMath.round(curost - curotp, 3);
                        }
                        UpdateHighlite(qrsx.selected() + i);
                    }
                } else {
                    qrsx.setFloat("OTP", curost);
                }
                qrsx.drawgrid();
            }
            if (requestCode == REQ_MESSAGE) {
                    setResult(RESULT_OK, intent);
                    activity.finish();
            }
            if (requestCode == REQ_ZAM) {

                String pkizam = intent.getStringExtra("pkizam");
                String cellzam = intent.getStringExtra("cellzam");
                String namezam = intent.getStringExtra("namezam");

                boolean zamall = intent.getBooleanExtra("zamall",false);
                float ostzam = Float.valueOf(intent.getStringExtra("ostzam"));
                float cntzam = Float.valueOf(intent.getStringExtra("cntzam"));
                String edzam = intent.getStringExtra("edzam");

                if (zamall) {
                    String curpki = qrsx.getString("PKI");
                    for (int i = 0; i < qrsx.recordcount(); i++) {
                        qrsx.deselect();
                        if (qrsx.getString(i,"PKI").equals(curpki)) {
                            qrsx.select(i);
                            qrsx.setString("OLDPKI", qrsx.getString("PKI"));
                            qrsx.setFloat("OLDTREB", qrsx.getFloat("TREB"));
                            qrsx.setString("OLDCELL", qrsx.getString("CELL"));
                            qrsx.setString("OLDNAME", qrsx.getString("NAMEPKI"));
                            qrsx.setFloat("OLDOST", qrsx.getFloat("OST"));

                            //добавление замены
                            qrsx.setString("PKI", pkizam);
                            qrsx.setFloat("TREB", qrsx.getFloat("TREB") * cntzam);
                            qrsx.setFloat("OTP", qrsx.getFloat("TREB") * cntzam);

                            qrsx.setString("CELL", cellzam);
                            qrsx.setString("NAMEPKI", namezam);
                            qrsx.setFloat("OST", ostzam);
                            UpdateHighlite(qrsx.selected());
                        }
                    }
                } else {
                    //сохранение данных по позиции состава
                    qrsx.setString("OLDPKI", qrsx.getString("PKI"));
                    qrsx.setFloat("OLDTREB", qrsx.getFloat("TREB"));
                    qrsx.setString("OLDCELL", qrsx.getString("CELL"));
                    qrsx.setString("OLDNAME", qrsx.getString("NAMEPKI"));
                    qrsx.setFloat("OLDOST", qrsx.getFloat("OST"));

                    //добавление замены
                    qrsx.setString("PKI", pkizam);
                    qrsx.setFloat("TREB", qrsx.getFloat("TREB") * cntzam);
                    qrsx.setFloat("OTP", qrsx.getFloat("TREB") * cntzam);

                    qrsx.setString("CELL", cellzam);
                    qrsx.setString("NAMEPKI", namezam);
                    qrsx.setFloat("OST", ostzam);
                    UpdateHighlite(qrsx.selected());
                }
                qrsx.drawgrid();
            }
            if (requestCode == REQ_CLOSEWIND) {
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK, intent);
                    activity.finish();
                }
            }
            if (requestCode == REQ_CREATEDEFICIT) {
                if (resultCode == RESULT_OK) {
                    createDocument();
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
        //Для всех остальных опций должна быть выбрана позиция
        if (qrsx.selected()==-1) {
            return super.onOptionsItemSelected(item);
        }
        //Показываем позиции замены
        if (item.getItemId() == R.id.act_zam) {

            Intent intent = new Intent(activity, Activity_zam.class);
            intent.putExtra("PKI", qrsx.getString("PKI"));
            intent.putExtra("SKLAD", skladin);
            intent.putExtra("ZAMALL", false);
            startActivityForResult(intent, REQ_ZAM);
        }
        //Замены всех одинаковых позиций
        if (item.getItemId() == R.id.act_zam_all) {

            Intent intent = new Intent(activity, Activity_zam.class);
            intent.putExtra("PKI", qrsx.getString("PKI"));
            intent.putExtra("SKLAD", skladin);
            intent.putExtra("ZAMALL", true);
            startActivityForResult(intent, REQ_ZAM);
        }

        //Возврат замены
        if (item.getItemId()==R.id.act_notzam) {
            if (qrsx.getString("OLDPKI")!=null) {
                qrsx.setString("PKI", qrsx.getString("OLDPKI"));
                qrsx.setFloat("TREB", qrsx.getFloat("OLDTREB"));
                qrsx.setString("CELL", qrsx.getString("OLDCELL"));
                qrsx.setString("NAMEPKI", qrsx.getString("OLDNAME"));
                qrsx.setFloat("OST",qrsx.getFloat("OLDOST"));
                qrsx.setString("OLDPKI", null);
                UpdateHighlite(qrsx.selected());
                qrsx.drawgrid();
            }
         }

        if (item.getItemId()==R.id.act_cnt) {
            Intent intent = new Intent(activity, cnt.class);
            intent.putExtra("namepki",qrsx.getString("PKI"));
            intent.putExtra("treb",qrsx.getFloat("TREB"));
            intent.putExtra("otp",qrsx.getFloat("OTP"));
            intent.putExtra("scan", false);
            startActivityForResult(intent,REQ_CNT);
        }
        return super.onOptionsItemSelected(item);
    }


    public void saveRsx() {
            StringBuilder errString = new StringBuilder();
            if (!testData(errString)) {
                if (deficit) {
                    Intent intent = new Intent(activity, messageyesno.class);
                    intent.putExtra("message", errString.toString().concat("Создать дефицитное требование?"));
                    startActivityForResult(intent, REQ_CREATEDEFICIT);
                } else {
                    Intent intent = new Intent(activity, message.class);
                    intent.putExtra("message", errString.toString());
                    startActivityForResult(intent, ERR_MESSAGE);
                }
            } else {
                createDocument();
            }
    }

    void createDocument() {
        qSaveRsx = new AnoQuery(activity, R.raw.qsaversx, prochandler);
        StringBuilder sql = new StringBuilder();

        sql.append("v_skladin := '" + skladin + "';");
        sql.append("v_skladout := '" + skladout + "';");
        sql.append("v_folder := '" + folder + "';");
        sql.append("v_operator := '" + mApp.userId + "';");
        for (int idx = 0; idx < qrsx.getData().size(); idx++) {
            String pki = (String) qrsx.getString(idx,"PKI");
            String itc = Float.toString(qrsx.getFloat(idx,"OTP"));
            String mgnbr = (String) qrsx.getString(idx,"DOCNUM");
            String mglot = (String) qrsx.getString(idx,"INDNUM");
            String spz = (String) qrsx.getString(idx,"SHPZ");

            if (qrsx.getFloat(idx,"OTP") != 0) {
                sql.append("insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd)values('" + pki + "', " + itc + ", '" + mgnbr + "', '" + mglot + "', " + idx + ", '" + spz + "', null, null);");
                sql.append("cntrec := cntrec + 1;");
            }
        }
        qSaveRsx.setMacro("macroparams", sql.toString());
        qSaveRsx.Open();
    }

    public boolean testData(StringBuilder s) {
        boolean result = true;
        try {
            deficit = false;
            for (int idx = 0; idx < qrsx.getData().size(); idx++) {
                float fotp = qrsx.getFloat(idx, "OTP");
                float fost = qrsx.getFloat(idx, "OST");;
                float ftreb = qrsx.getFloat(idx, "TREB");
                String pki   = qrsx.getString(idx, "PKI");
                if (fotp < ftreb) {
                    deficit = true;
                    result = false;
                    s.append("ПКИ "+pki+": есть неотпущенное количество\n");
                }
                if (fotp>fost) {
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
        if (operationstarted) {
            Intent intent = new Intent(activity, messageyesno.class);
            intent.putExtra("message", "Прервать операцию?");
            startActivityForResult(intent, REQ_CLOSEWIND);
        } else {
            activity.finish();
        }
    }
}