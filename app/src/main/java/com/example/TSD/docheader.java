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

import com.example.mApp;
import com.example.myapplication111.R;

import java.util.HashMap;
import java.util.Map;

public class docheader extends AppCompatActivity {

    private  static  int REQ_NEXT = 1;
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
        setTitle("Кладовая ОВК - ".concat(mApp.userId));

        txtSkladin = findViewById(R.id.txtSkladin);
        txtBatch = findViewById(R.id.txtBatch);
        edtSkladout = findViewById(R.id.edtSkladout);
        edtFolder = findViewById(R.id.edtFolder);
        btnSubmit = findViewById(R.id.btnSubmit);

        Bundle arguments = getIntent().getExtras();
        batch = arguments.get("BATCH").toString();
        sklad = arguments.get("SKLAD").toString();

        txtBatch.setText(batch);
        txtSkladin.setText(sklad);
        edtSkladout.setText("00099");
        edtSkladout.selectAll();
    }

    public void onSubmitClick(View view)
    {
        Intent intent = new Intent(activity, rsx.class);
        intent.putExtra("batch",batch);
        intent.putExtra("skladin",sklad);
        intent.putExtra("skladout",edtSkladout.getText().toString());
        intent.putExtra("folder",edtFolder.getText().toString());
        startActivityForResult(intent,REQ_NEXT);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQ_NEXT) {
            if (resultCode == RESULT_OK) {
                super.onActivityResult(requestCode, resultCode, intent);
                setResult(RESULT_OK);
                finish();
            }
        }
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