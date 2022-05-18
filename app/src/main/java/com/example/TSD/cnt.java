package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;


import com.example.myapplication111.R;

import java.util.ArrayList;
import java.util.Map;

import static java.util.Objects.isNull;

public class cnt extends AppCompatActivity {

    private TextView tvPki;
    private EditText tCnt;
    private TextView tTreb;
    private Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnt);

        tvPki = findViewById(R.id.tvPki);
        tTreb = findViewById(R.id.tvTreb);
        tCnt = findViewById(R.id.tCnt);

        Bundle arguments = getIntent().getExtras();
        tvPki.setText(arguments.get("namepki").toString());
        tTreb.setText(arguments.get("treb").toString());
        tCnt.setText(arguments.get("otp").toString());
        tCnt.setSelection(tCnt.getText().length());

        tCnt.setOnEditorActionListener(cntEditorActionListener);
    }

    TextView.OnEditorActionListener cntEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
            try {
                Intent intent = new Intent();
                intent.putExtra("otp", tCnt.getText().toString());
                setResult(RESULT_OK, intent);
                activity.finish();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }
    };




}