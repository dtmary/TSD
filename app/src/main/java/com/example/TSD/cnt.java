package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;


import com.example.mApp;
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
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_cnt);
            setTitle("Кладовая ОВК - ".concat(mApp.userId));

            tvPki = findViewById(R.id.tvPki);
            tTreb = findViewById(R.id.tvTreb);
            tCnt = findViewById(R.id.tCnt);

            Bundle arguments = getIntent().getExtras();
            tvPki.setText(arguments.get("namepki").toString());
            tTreb.setText(arguments.get("treb").toString());
            if (Integer.valueOf(arguments.get("otp").toString())==0) {
                tCnt.setText(arguments.get("treb").toString());
            } else {
                tCnt.setText(arguments.get("otp").toString());
            }
            tCnt.selectAll();
            tCnt.setOnEditorActionListener(cntEditorActionListener);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    TextView.OnEditorActionListener cntEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
            try {
                if (Float.valueOf(tCnt.getText().toString()) > Float.valueOf(tTreb.getText().toString())) {
                    mApp.mSoundPool.play(mApp.soundIdbad, 1, 1, 1, 0, 1f);
                    Intent intent = new Intent(activity, message.class);
                    intent.putExtra("message", "Отпущенное кол-во не может быть больше требуемого");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("otp", tCnt.getText().toString());
                    setResult(RESULT_OK, intent);
                    activity.finish();
                }
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }
    };




}