package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.example.TSD.AnO.AnoQuery;
import com.example.myapplication111.R;

public class ActivityPkiInfo extends AppCompatActivity {

    private String pki;
    private TextView tvPki;
    private TextView tvNamepki;
    private TextView tvMestpol;
    private TextView tvOstatok;
    private AnoQuery qpkiinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pki_info);
        tvPki = findViewById(R.id.tvPki);
        tvNamepki = findViewById(R.id.tvNamepki);
        tvMestpol = findViewById(R.id.tvMestpol);
        tvOstatok = findViewById(R.id.tvOstatok);

        Bundle arguments = getIntent().getExtras();
        qpkiinfo = new AnoQuery(this, R.raw.qpkiinfo);
        qpkiinfo.setParamString("PKI", arguments.get("pki").toString());
        qpkiinfo.sethAfterOpen(new Handler(getBaseContext().getMainLooper()) {
            public void handleMessage(Message msg) {
                qpkiinfo.select(0);
                tvPki.setText(qpkiinfo.getString("PKI"));
                tvNamepki.setText(qpkiinfo.getString("NAMEPKI"));
                tvMestpol.setText("Склад:".concat(qpkiinfo.getString("SKLAD")));
                tvOstatok.setText("Остаток:".concat(qpkiinfo.getString("OSTATOK")));
            }
        }
        );
        qpkiinfo.Open();
    }
}