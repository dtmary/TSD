package com.example.myapplication111.AnO;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.widget.TextView;

import com.example.myapplication111.MainActivity;
import com.example.myapplication111.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

public class AnoQuery {

    //TODO: таймаут попытки соединения с ораклом

    public static final int stclosed = 0;
    public static final int stexecuted = 1;
    public static final int stactive = 2;

    private static boolean connected = false;

    private int _status = 0;
    public ResultSet resultSet;
    private static Connection dbconnection = null;
    public String sql;
    public Activity activity;
    private String jsql;
    public Map<String,SQLParameter>  params;
    private char paramSimb = ':';

    //TODO: конвертация запроса, работа с параметрами

    public void setParamString(String ParamName, String ParamValue) {
        SQLParameter param = params.get(ParamName.toUpperCase());
        param.setString(ParamValue);
    }

    public void setParam(String ParamName, Object ParamValue) {
        SQLParameter param = params.get(ParamName.toUpperCase());
        param.set(ParamValue);
    }

    private String parseParam(int pos) {
        int cur = pos+1;
        String paramName = "";
        char c = sql.charAt(cur);
        while (((c >= 'a')&&(c <= 'z')) || ((c >= 'A')&&(c <= 'Z')) || (c == '_')){
            paramName = paramName+sql.charAt(cur);
            cur++;
            c = sql.charAt(cur);
        }
        return paramName;
    }

    private void parseParams () {
        Map paramPositions = new HashMap();
        SQLParameter param;
        int curParam = 0;
        for(int i = 0; i < sql.length(); i++){
            if (sql.charAt(i) == paramSimb) {
                curParam++;
                String paramName = parseParam(i);
                paramPositions.put(i,paramName);
                param = params.get(paramName.toUpperCase());
                if (param == null) {
                    param = new SQLParameter(paramName.toUpperCase());
                    params.put(paramName.toUpperCase(), param);
                }
                param.addPos(curParam);
            }
        }
        paramPositions.forEach((k, v) -> sql = sql.replaceAll(Character.toString(paramSimb)+ v, "?"));
    }

    public String  getStringFromRawFile(Integer id) {
        Resources r = activity.getResources();
        InputStream is = r.openRawResource(id);
        String myText = null;
        try {
            myText = convertStreamToString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  myText;
    }

    private String  convertStreamToString(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = is.read();
        while( i != -1)
        {
            baos.write(i);
            i = is.read();
        }
        return  baos.toString()+"  ";
    }

    private class InitDB extends AsyncTask<Void,Void,Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.5:1521:ORA","skladuser","sklad");
                connected = true;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            catch (Exception throwables) {
                throwables.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer i) {
            connected = true;
        }
    }

    public int status() {
        return _status;
    }

    public boolean active() {
        if (_status==stactive) {
            return (true);
        }
        else{
            return (false);
        }
    }

    public AnoQuery(Activity client, Integer SQLID) {
        activity = client;
        params = new HashMap<String,SQLParameter> ();
        sql = getStringFromRawFile(SQLID);
        parseParams();

        if (connected==false) {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            InitDB initDB = new InitDB();
            initDB.execute();
            while (connected==false) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };
        }
    }

    private class ExecQuery extends AsyncTask<Void,Void,Integer> {
        ResultSet rs = null;
        @Override
        protected Integer doInBackground(Void... voids) {
            _status = stexecuted;
            try {
                PreparedStatement stmt = null;
                stmt = dbconnection.prepareStatement(sql);
                for (Map.Entry<String, SQLParameter> pair : params.entrySet()) {
                    SQLParameter p = pair.getValue();
                    int j = 0;
                    while (p.positions.size() > j) {
                        Integer pos = p.positions.get(j);
                        String s = p.getString();
                        stmt.setString(pos, s);
                        j++;
                    }
                }

                stmt.execute();
                rs = stmt.getResultSet();
                stmt = null;
                _status = stactive;
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            catch (Exception throwables) {
                throwables.printStackTrace();
            }
            resultSet = rs;
            return 0;
        }
    }

    public void Open() {
        ExecQuery execQuery = new ExecQuery();
        execQuery.execute();
        while (_status != stactive) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public void Close() {
        resultSet = null;
        _status = stclosed;
    }
}
