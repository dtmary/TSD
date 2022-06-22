package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.Layout;
import android.widget.TextView;

import com.example.myapplication111.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        TextView testView = (TextView) findViewById(R.id.testView);

        //R.layout.grid_test
        XmlResourceParser parser = getResources().getLayout(R.layout.grid_test);
        String s = "";
        //for (int i = 0; i < parser.getAttributeCount(); i++) {
        //    s = s.concat(",".concat(parser.));
        //}


        //testView.setText(s);
        //String s = getResources().getResourceEntryName(R.id.titleskl);

        //TextView titleskl = (TextView) findViewById(R.id.titleskl);
        //titleskl.setText(s);
    }
}