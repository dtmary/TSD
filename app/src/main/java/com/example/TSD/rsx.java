package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

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

    private String from[] = {attrnamepki,attrcell, attrtreb,attrost,attrotp,attrheadizd};
    private int to[] = {R.id.pki,R.id.cell ,R.id.treb,R.id.ost,R.id.otp,R.id.headizd};

    private Activity activity = this;
    private Handler handler;
    private ListView ltRoot;
    private String scanCode = "";
    private EditText tScan;
    private SoundPool mSoundPool;
    private int soundIdbad;
    private int curPos;
    ArrayList<Map<String, Object>> data;

    private class SAdapter extends SimpleAdapter {
        public SAdapter(rsx rsx, ArrayList<Map<String, Object>> data, int sostrow, String[] from, int[] to) {
            super(activity, data, R.layout.sostrow, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            HashMap rec = (HashMap)data.get(position);
            String sTreb = (String) rec.get(attrtreb);
            String sOtp =  (String) rec.get(attrotp);
            Float treb = Float.valueOf(sTreb);
            Float otp;
            if (sOtp.equals("")) {
                otp = 0.f;
            }
                else otp = Float.valueOf(sOtp);
            if (treb.equals(otp)) {
                view.setBackgroundResource(R.color.WhiteGreen);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rsx_menu, menu);
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
                    m.put(attrnamepki,qrsx.resultSet.getString(4).concat(" - ").concat(qrsx.resultSet.getString(5)));
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = item.getTitle().toString();
        if (item.getItemId()==R.id.act_rsx_save) {
            saveRsx();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveRsx() {
        StringBuilder sql = new StringBuilder();
        sql.append("DECLARE");

        sql.append("v_opnum skllog.opnum%type;");
        sql.append("v_docnum skllog.docnum%type;");
        sql.append("v_makeresult varchar2(255);");
        sql.append("v_saveresult boolean;");
        sql.append("number;");
        sql.append("v_docdate varchar2(10);");
        sql.append("v_docform varchar2(255);");
        sql.append("v_company_id number;");
        sql.append("v_kodoper number;");

        sql.append("v_skladin varchar2(5);");
        sql.append("v_skladout varchar2(5);");
        sql.append("v_iscalcnal_r number;");
        sql.append("v_in_calcwithreserv number;");
        sql.append("v_folder varchar2(10);");

        sql.append("v_ppkib_rec pcgsklad.ppkib_rec_type;");
        sql.append("v_ppki_rec pcgsklad.ppki_rec_type;");
        sql.append("v_pcshid_rec pcgsklad.pcshid_rec_type;");
        sql.append("v_postatok_rec pcgsklad.postatok_rec_type;");
        sql.append("v_pitem_count_rec pcgsklad.pitem_count_rec_type;");
        sql.append("v_pprice_rec pcgsklad.pprice_rec_type;");
        sql.append("v_psumma_rec pcgsklad.psumma_rec_type;");
        sql.append("v_paccd_rec pcgsklad.paccd_rec_type;");
        sql.append("v_paccc_rec pcgsklad.paccc_rec_type;");
        sql.append("v_pmg_nbr_rec pcgsklad.pmg_nbr_rec_type;");
        sql.append("v_pmg_lot_rec pcgsklad.pmg_lot_rec_type;");
        sql.append("v_ptyp_pkib_rec pcgsklad.ptyp_pkib_rec_type;");
        sql.append("v_pspz_rec pcgsklad.pspz_rec_type;");
        sql.append("i integer;");
        sql.append("begin");

        sql.append("v_docdate := trunc(sysdate,'DAY');");
        sql.append("v_docform := fonds.readsetting(in_sect => 'skl_DocForms',in_ident => 'RsxSkl',in_company_id => 1,in_loginid => 0);");
        sql.append("v_company_id := 1;");
        sql.append("v_kodoper := 133;");
        sql.append("v_opnum := 0;");

        //Параметры

        sql.append("v_skladin := '00203';");
        sql.append("v_skladout := '00203';");
        sql.append("v_iscalcnal_r := 0;");
        sql.append("v_in_calcwithreserv := 0;");
        sql.append("v_folder := '';");
        sql.append("v_docnum := 0;");
        sql.append("cntrec := 0;");

        for (int idx = 0; idx < data.size(); idx++) {
            Map<String, Object> m = (HashMap)data.get(idx);
            String pki = (String)m.get(attrpki);
            String itc = (String)m.get(attrotp);
            String mgnbr = (String)m.get(attrmgnbr);
            String mglot = (String)m.get(attrmglot);
            String spz = (String)m.get(attrshpz);

            if (!itc.equals("0")) {
                sql.append("insert into pkibsklrasp(pki, item_count, mg_nbr, mg_lot, recid, spz, accc, accd)values('" + pki + "', " + itc + ", '" + mgnbr + "', '" + mglot + "', " + idx + ", '" + spz + "', null, null);");
                sql.append("cntrec := cntrec + 1;");
            }
        }

        sql.append("v_makeresult := pcgsklad.makepkibskl(in_docdate => v_docdate,");
        sql.append("in_opnum => 0,");
        sql.append("in_skladin => v_skladin,");
        sql.append("in_calcwithreserv => v_in_calcwithreserv,");
        sql.append("in_company_id => v_company_id,");
        sql.append("iscalcnal_r => v_iscalcnal_r,");
        sql.append("in_kodoper => v_kodoper);");

        sql.append("i := 0;");
        sql.append("for rec in (select * from skladuser.pkibsklraspres p) loop");
        sql.append("i := i + 1;");
        sql.append("v_ppkib_rec(i) := rec.pkib;");
        sql.append("v_ppki_rec(i) := rec.pki;");
        sql.append("v_pcshid_rec(i) := rec.schid;");
        sql.append("v_postatok_rec(i) := 0;");
        sql.append("v_pitem_count_rec(i) := rec.item_count;");
        sql.append("v_pprice_rec(i) := rec.price;");
        sql.append("v_psumma_rec(i) := rec.summa;");
        sql.append("v_paccd_rec(i) := rec.accd;");
        sql.append("v_paccc_rec(i) := rec.accc;");
        sql.append("v_pmg_nbr_rec(i) := rec.mg_nbr;");
        sql.append("v_pmg_lot_rec(i) := rec.mg_lot;");
        sql.append("v_ptyp_pkib_rec(i) := 0;");
        sql.append("v_pspz_rec(i) := rec.spz;");
        sql.append("end loop;");

        sql.append("v_saveresult := pcgsklad.savesklrsx(in_action => 0,");
        sql.append("in_opnum => v_opnum,");
        sql.append("in_docdate => v_docdate,");
        sql.append("in_docnum => v_docnum,");
        sql.append("in_company_id => v_company_id,");
        sql.append("in_skladout => v_skladout,");
        sql.append("in_skladin => v_skladin,");
        sql.append("in_oper => v_kodoper,");
        sql.append("in_user_id => 'TERMINAL',");
        sql.append("is_otlog => 'N',");
        sql.append("in_par_opnum => null,");
        sql.append("in_kod_post => null,");
        sql.append("in_docfrm => v_docform,");
        sql.append("in_cbfolder => null,");
        sql.append("in_edtfolder => v_folder,");
        sql.append("in_skl_rec_count => cntrec,");
        sql.append("ppkib_rec => v_ppkib_rec,");
        sql.append("ppki_rec => v_ppki_rec,");
        sql.append("pcshid_rec => v_pcshid_rec,");
        sql.append("postatok_rec => v_postatok_rec,");
        sql.append("pitem_count_rec => v_pitem_count_rec,");
        sql.append("pprice_rec => v_pprice_rec,");
        sql.append("psumma_rec => v_psumma_rec,");
        sql.append("paccd_rec => v_paccd_rec,");
        sql.append("paccc_rec => v_paccc_rec,");
        sql.append("pmg_nbr_rec => v_pmg_nbr_rec,");
        sql.append("pmg_lot_rec => v_pmg_lot_rec,");
        sql.append("ptyp_pkib_rec => v_ptyp_pkib_rec,");
        sql.append("pspz_rec => v_pspz_rec);");
        sql.append("end;");

        String sSql = sql.toString();
    }
}