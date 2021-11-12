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

    public void execproc() {
        String sql;
        //Создаём SQL
        sql = "DECLARE";
        for (int i = 0; params.size() < i; i++) {
            Param param = params.get(i);
            sql = sql + "\n"+param.name+" "+param.sqlType+";";
        }
        sql = sql + "\n" + "begin";
    }

}
