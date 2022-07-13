package com.example.TSD.AnO;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.myapplication111.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AnoAdapter extends SimpleAdapter {

    private ArrayList<Map<String, Object>> data;


    public AnoAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.data = (ArrayList<Map<String, Object>>) data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundResource((Integer) data.get(position).get("COLOR"));
        return view;
    }
}
