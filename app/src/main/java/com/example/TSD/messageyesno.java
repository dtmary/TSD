package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication111.R;

public class messageyesno extends AppCompatActivity {

    TextView mesView;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messageyesno);
        activity = this;
        mesView = (TextView) findViewById(R.id.mesView);
        Bundle arguments = getIntent().getExtras();
        mesView.setMovementMethod(new ScrollingMovementMethod());
        mesView.setText(arguments.get("message").toString());
    }

    public void onYesClick(View view) {
        try {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            activity.finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void onNoClick(View view) {
        try {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            activity.finish();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


}