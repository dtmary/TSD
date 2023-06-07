package com.example.TSD.AnO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.TSD.message;
import com.example.TSD.rsx;
import com.example.mApp;
import com.example.myapplication111.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import static android.provider.Settings.System.getString;
import static com.example.mApp.testmode;
import static java.lang.Thread.sleep;

public class AnoQuery {

    public static final int stclosed = 0;
    public static final int stexecuted = 1;
    public static final int stactive = 2;
    public static final int statSuccessfully = 20999;


    private static boolean connected = false;

    private int _status = 0;
    public ResultSet resultSet;
    private static Connection dbconnection;
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
    private Handler hBeforeDrawGrid;
    private Handler hAfterOpen;
    public void sethBeforeDrawGrid(Handler h) {
        hBeforeDrawGrid = h;
    }
    public void sethAfterOpen(Handler h) {
        hAfterOpen = h;
    }
    public int rowlayout;
    public String[] from;
    public int[] to;
    public ListView view;
    public AnoAdapter adapter;
    private int selected = -1;

    private String[] fields;
    private ArrayList<Map<String, Object>> data;

    public String[] getFields() {
        return fields;
    }

    public ArrayList<Map<String, Object>> getData() {
        return data;
    }

    public int GetResultCode() {return resultcode;}
    public String GetResultMessage() {return resultmessage;}

    public int recordcount() {
        return _recordcount;
    }

    public static void disconnect() throws SQLException {
        dbconnection = null;
        connected = false;
    }

    public static Connection getDbconnection() {
        return  dbconnection;
    }

    public void setString (int RecNo, String fieldName, String value) {
        Map<String, Object> m = (HashMap) getData().get(RecNo);
        m.put(fieldName,value);
    }

    public void setString (String fieldName, String value) {
        setString (selected(), fieldName, value);
    }

    public void setFloat (int RecNo, String fieldName, float value) {
        Map<String, Object> m = (HashMap) getData().get(RecNo);
        m.put(fieldName,value);
    }

    public void setFloat (String fieldName, float value) {
        setFloat (selected(), fieldName, value);
    }

    public int getInt(int RecNo, String fieldName) {
        Map<String, Object> m = (HashMap) getData().get(RecNo);
        return Integer.valueOf(String.valueOf(m.get(fieldName)));
    }

    public int getInt(String fieldName) {
        return getInt(selected(), fieldName);
    }

    public String getString(int RecNo, String fieldName) {
        Map<String, Object> m = (HashMap) getData().get(RecNo);
        return (String) m.get(fieldName);
    }

    public String getString(String fieldName) {
        return getString(selected(),fieldName);
    }

    public float getFloat(int RecNo, String fieldName) {
        Map<String, Object> m = (HashMap) getData().get(RecNo);
        return Float.valueOf(String.valueOf(m.get(fieldName)));
    }

    public float getFloat(String fieldName) {
        return getFloat(selected(),fieldName);
    }

    public void deselect() {
        for (int i = 0; i < data.size(); i++) {
            try {
                view.getChildAt(i-view.getFirstVisiblePosition()).setBackgroundResource((Integer) data.get(i).get("DESELECTEDCOLOR"));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            data.get(i).put("COLOR",data.get(i).get("DESELECTEDCOLOR"));
        }
        selected = -1;
    }

    public void select(int position) {
        try {
            data.get(position).put("COLOR", R.color.Selected);
            view.getChildAt(position - view.getFirstVisiblePosition()).setBackgroundResource(R.color.Selected);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        selected = position;
    }

    public int selected() {
        return selected;
    }

    public void setColor(int position, int color) {
        try {
            data.get(position).put("DESELECTEDCOLOR", color);
            if (selected!=position) {
                data.get(position).put("COLOR", color);
                view.getChildAt(position - view.getFirstVisiblePosition()).setBackgroundResource(color);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
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

    private class InitDB extends Thread  {
        public void run() {
            while (!connected) {
                try {
                    //Реальный
                    if (mApp.testmode) {
                        //Тестовый
                        dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.105:1521:ORA","skladuser","sklad");
                    } else {
                        //Реальный
                        dbconnection = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.5:1521:ORA", "skladuser", "sklad");
                    }
                    connected = true;
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
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
        } catch (Exception e) {
        e.printStackTrace();
        }
    }

    //Конструктор для отображения списка
    public AnoQuery(Activity client, Integer SQLID, int inRowlayout,String[] infrom, int[] into, ListView inView) {
        try {
            activity = client;
            params = new HashMap<String,SQLParameter> ();
            sql = getStringFromRawFile(SQLID);
            rowlayout = inRowlayout;
            from = infrom;
            to = into;
            view = inView;
            parseParams();
            handler = new Handler(activity.getMainLooper()) {
                public void handleMessage(Message msg) {
                    if (hBeforeDrawGrid!=null) {hBeforeDrawGrid.sendEmptyMessage(0);};
                    drawgrid();
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void drawgrid() {
        adapter = new AnoAdapter(activity, data, rowlayout, from, to);
        view.setAdapter(adapter);
        if (selected > -1) {view.setSelection(selected);}
    }

    private class ExecQuery extends Thread {
        public void run() {
            resultSet = null;
            _status = stexecuted;
            try {
                connected = (dbconnection!=null);
                if (connected) {
                    connected = (dbconnection.isValid(0));
                }
                if (!connected) {
                    try {
                        Class.forName("oracle.jdbc.driver.OracleDriver");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    InitDB initDB = new InitDB();
                    initDB.start();
                    while (connected==false) {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    };
                }

                PreparedStatement stmt = null;
                stmt = dbconnection.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
                resultSet = stmt.getResultSet();
                stmt = null;

            } catch (SQLException e) {
                resultcode = e.getErrorCode();
                resultmessage = e.getMessage();
                resultmessage = resultmessage.substring(10);
                int pos = resultmessage.indexOf("\nORA");
                resultmessage = resultmessage.substring(1,pos);
                if (resultcode != statSuccessfully) {
                    try {
                        dbconnection.rollback();
                        dbconnection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    Intent intent = new Intent(activity, message.class);
                    intent.putExtra("message", resultmessage);
                    activity.startActivityForResult(intent,0);
                }

            }
            catch (Exception throwables) {
                throwables.printStackTrace();
            }
            if (resultSet!=null) {
                resultcode = statSuccessfully;
                resultmessage = "";
              try {
                    resultSet.last();
                    _recordcount = resultSet.getRow();
                    resultSet.first();
                    fields = new String[resultSet.getMetaData().getColumnCount()];
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        fields[i-1] = resultSet.getMetaData().getColumnName(i);
                    }
                    data = new ArrayList<Map<String, Object>>(_recordcount);
                    Map<String, Object> m;
                    resultSet.first();
                    do {
                        m = new HashMap<String, Object>();
                        for (int i = 0; i < fields.length; i++) {
                            m.put(fields[i],resultSet.getString(fields[i]));
                        }
                        m.put("COLOR",R.color.white);
                        m.put("DESELECTEDCOLOR",R.color.white);
                        data.add(m);
                    } while (resultSet.next());
                    if (hAfterOpen!=null) {hAfterOpen.sendEmptyMessage(0);}
                } catch (Throwable throwables) {
                    throwables.printStackTrace();
                }
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
