package com.example.myapplication111.AnO;

import java.util.ArrayList;
import java.util.List;

public class SQLParameter {

    String paramName;
    int    typeVal;
    // 1 - String
    // 2 - Float
    // 3 - int
    Object val;
    String sVal;

    public List<Integer> positions;

    public SQLParameter (String paramName) {
        this.paramName = paramName;
        positions = new ArrayList<>();
    }

    public void addPos(int pos){
        positions.add(pos);
    }

    public void set(Object newVal){
        val = newVal;
    }

    public Object get(){
        return val;
    }

    public String getString() {
        return sVal;
    }

    public void setString(String newVal){
        val = newVal;
        typeVal = 1;
        sVal = newVal;
    }

    public void setFloat(Float newVal){
        val = newVal;
        typeVal = 2;
    }

    public void setInt(Integer newVal){
        val = newVal;
        typeVal = 3;
    }

}

