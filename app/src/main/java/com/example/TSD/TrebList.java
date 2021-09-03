package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.myapplication111.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class TrebList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treb_list);
        ButterKnife.bind(this);


    }
}