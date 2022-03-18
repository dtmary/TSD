package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication111.R;

public class message extends AppCompatActivity {

    TextView mesView;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        activity = this;
        mesView = (TextView)findViewById(R.id.mesView);
        Bundle arguments = getIntent().getExtras();
        mesView.setText(arguments.get("batch").toString());
    }


    public void onSubmitClick(View view)
    {
        activity.finish();
    }
}