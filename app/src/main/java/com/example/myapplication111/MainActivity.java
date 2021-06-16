package com.example.myapplication111;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.myapplication111.AnO.AnoQuery;
import com.example.myapplication111.databinding.ActivityMainBinding;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

public class MainActivity extends AppCompatActivity    {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Connection dbconnection = null;
    private Activity activity = this;
    private AnoQuery qPki;
    private Button btnq;
    private TextView ts;
    private EditText edtPKI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Инициализация переменных
        btnq = findViewById(R.id.btnq);
        ts = findViewById(R.id.ts);
        edtPKI =  (EditText) findViewById(R.id.edtPKI);
        qPki = new AnoQuery(activity,R.raw.qpki);

        //Назначение обработчиков
        btnq.setOnClickListener(btnqOnClickListener);

        edtPKI.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == KeyEvent.KEYCODE_HOME){
                    ts.setText(edtPKI.getText().toString());
                    // обработка нажатия Enter
                    //return true;
                }
                return false;
            }});
    }

    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(String.valueOf(ts.getId()), (Serializable) ts.getText());
        outState.putSerializable(String.valueOf(edtPKI.getId()), (Serializable) edtPKI.getText().toString());
        super.onSaveInstanceState(outState);
    }

    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ts.setText(savedInstanceState.getString(String.valueOf(ts.getId())));
        edtPKI.setText(savedInstanceState.getString(String.valueOf(edtPKI.getId())));
    }

    View.OnClickListener btnqOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                qPki.Open();
                ts.setText(qPki.resultSet.getString(3));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    };
}