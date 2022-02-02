package com.example.TSD;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication111.R;

import java.util.HashMap;
import java.util.Map;

public class docheader extends AppCompatActivity {
    String batch;
    String sklad;
    TextView txtSkladin;
    TextView txtBatch;
    EditText edtSkladout;
    EditText edtFolder;
    Button btnSubmit;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docheader);

        txtSkladin = findViewById(R.id.txtSkladin);
        txtBatch = findViewById(R.id.txtBatch);
        edtSkladout = findViewById(R.id.edtSkladout);
        edtFolder = findViewById(R.id.edtFolder);
        btnSubmit = findViewById(R.id.btnSubmit);

        Bundle arguments = getIntent().getExtras();
        batch = arguments.get("batch").toString();
        sklad = arguments.get("sklad").toString();

        txtBatch.setText(batch);
        txtSkladin.setText(sklad);
    }

    public void onSubmitClick(View view)
    {
        Intent intent = new Intent(activity, rsx.class);
        intent.putExtra("batch",batch);
        intent.putExtra("skladin",sklad);
        intent.putExtra("skladout",edtSkladout.getText().toString());
        intent.putExtra("folder",edtFolder.getText().toString());
        startActivity(intent);
    }

    TextView.OnEditorActionListener cntEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
            try {
                activity.finish();
            }
            catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }
    };


}