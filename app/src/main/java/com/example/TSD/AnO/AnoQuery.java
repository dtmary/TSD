package com.example.TSD.AnO;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
import java.util.Objects;

import static android.provider.Settings.System.getString;
import static java.lang.Thread.sleep;

public class AnoQuery {

    //TODO: таймаут попытки соединения с ораклом

    public static final int stclosed = 0;
    public static final int stexecuted = 1;
    public static final int stactive = 2;
    public static final int statSuccessfully = 20999;

    private static boolean connected = false;

    private int _status = 0;
    public ResultSet resultSet;
    private static Connection dbconnection = null;
    public String sql;
    public Activity activity;
    public Map<String,SQLParameter>  params;
    private char paramSimb = ':';
    private char macroSimb = '&';
    private char equalSimb = '=';
    private  int _recordcount = 0;
    private int resultcode;
    private String resultmessage;
    private Handler handler;

    public int GetResultCode() {return resultcode;}
    public String GetResultMessage() {return resultmessage;}

    public int recordcount() {
        return _recordcount;
    }

    public void setParamString(String ParamName, String ParamValue) {
        SQLParameter param = params.get(ParamName.toUpperCase());
        param.setString(ParamValue);
    }

    public void setParam(String ParamName, Object ParamValue) {
        SQLParameter param = params.get(ParamName.toUpperCase());
        param.set(ParamValue);
    }

    public void setMacro(String macroName, String macroValue) {
        String m = macroSimb+macroName;
        Integer macropos = sql.indexOf(m);
        StringBuilder sb = new StringBuilder();
        sb.append(sql.substring(0,macropos));
        sb.append(macroValue);
        sb.append(sql.substring(macropos+macroName.length()+1));
        sql = sb.toString();
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
            if (sql.charAt(i) == paramSimb&&sql.charAt(i+1) != equalSimb) {
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
                //Реальный dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.5:1521:ORA","skladuser","sklad");
                //Тестовый dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.105:1521:ORA","skladuser","sklad");
                dbconnection = DriverManager.getConnection(activity.getString(R.string.oraconnectionreal),
                                                            activity.getString(R.string.oralogin),
                                                            activity.getString(R.string.orapassword));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            catch (Exception throwables) {
                throwables.printStackTrace();
            }
            connected = true;
            return 0;
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

    public AnoQuery(Activity client, Integer SQLID, Handler h) {
        this(client, SQLID);
        this.handler = h;
    }

    public AnoQuery(Activity client, Integer SQLID) {
        try {
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
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    private class ExecQuery extends Thread {
        ResultSet rs = null;
        public void run() {
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

            } catch (SQLException e) {
                resultcode = e.getErrorCode();
                resultmessage = e.getMessage();
                if (resultcode == statSuccessfully) {
                    int pos = resultmessage.indexOf("\n");
                    resultmessage = resultmessage.substring(1,pos);
                    resultmessage = resultmessage.substring(10);
                }
            }
            catch (Exception throwables) {
                throwables.printStackTrace();
            }
            if (rs!=null) {
                resultcode = statSuccessfully;
                resultmessage = "";
              try {
                    rs.last();
                    _recordcount = rs.getRow();
                    rs.first();
              } catch (SQLException throwables) {
                  throwables.printStackTrace();
              }
              resultSet = rs;
            }
            if (!Objects.isNull(handler)) {
                handler.sendEmptyMessage(resultcode);
            }
            _status = stactive;
        }

    }

    public void Open() {
        Close();
        ExecQuery execQuery = new ExecQuery();
        execQuery.start();
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
