package com.example.TSD.AnO;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnoStoredProc {
    private class Param {
        public String name;
        public String type;
        public String sqlType;
        public boolean isArray;
        public ArrayList<Object> values = new ArrayList<Object>();
    }

    String PARAMFLOAT = "PARAMFLOAT";
    String PARAMSTRING = "PARAMSTRING";

    private Activity activity;
    ArrayList<Param> params = new ArrayList<Param>();
    Map<String, Param> mparams = new HashMap<String, Param>();

    public AnoStoredProc(Activity client) {
        activity = client;
    }

    private Param createParam(String paramname, String sqlType, boolean isArray) {
        Param param = new Param();
        param.name = paramname;
        param.sqlType = sqlType;
        param.isArray = isArray;
        params.add(param);
        mparams.put(paramname,param);
        return param;
    }

    public Param createStringParam(String paramname, String sqlType, boolean isArray) {
        Param param = createParam(paramname,sqlType, isArray);
        param.type = PARAMSTRING;
        return param;
    }

    public Param createFloatParam(String paramname, String sqlType, boolean isArray) {
        Param param = createParam(paramname,sqlType, isArray);
        param.type = PARAMFLOAT;
        return param;
    }

    public void setParamString(String paramname, String value) {
        Param param = mparams.get(paramname);
        param.values.add(value);
    }

    public void setParamFloat(String paramname, Float value) {
        Param param = mparams.get(paramname);
        param.values.add(value);
    }

    public String execproc() {
        String sql;
        //Создаём SQL
        sql = "DECLARE";
        for (int i = 0; (params.size() > i); i++) {
            Param param = params.get(i);
            sql = sql + " "+param.name+" "+param.sqlType+";";
        }
        sql = sql + " " + "begin";
        for (int i = 0; (params.size() > i); i++) {
            Param param = params.get(i);
            if (param.isArray) {
                for (int j = 0; (param.values.size() > j); j++) {
                    sql = sql + " " + param.name + "(" + String.valueOf(j) + "):="+String.valueOf(param.values.get(j));
                }
            } else {
                sql = sql + " " + param.name + ":=" + String.valueOf(param.values.get(0))+";";
            }
        }

        //TODO: заполнение параметров и вызов

        sql = sql + " " +"end;";
        return sql;
    }

}
