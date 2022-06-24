package com.example.TSD.AnO;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import com.example.myapplication111.R;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class AnoAdapter extends SimpleAdapter {

    private int[] colors;
    public Integer offset = 0;
    private static int selected = -1;

    public void select(int position) {
        colors[position] = R.color.Selected;
        selected = position;
    }

    public void deselect() {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = R.color.white;
            selected = -1;
        }
    }

    public int selected() {
        return selected;
    }

    public AnoAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        colors = new int[data.size()];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = R.color.white;
        }
        try {
            select(selected);
        } catch (Throwable e) {

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        view.setBackgroundResource(colors[position]);
        /*else {
            view.setBackgroundResource(R.color.white);
            HashMap rec = (HashMap) qrsx.getData().get(position);
            String sTreb = (String) rec.get("TREB");
            String sOtp = (String) rec.get("OTP");
            Float treb = Float.valueOf(sTreb);
            Float otp = 0.f;
            if (!sOtp.equals("")) {
                otp = Float.valueOf(sOtp);
            }
            if (treb.equals(otp)) {
                view.setBackgroundResource(R.color.WhiteGreen);
            }
            float fOst = 0;
            if (!((String) rec.get("OST")).equals("")) {
                fOst = Float.parseFloat((String) rec.get("OST"));
            }
            if (otp > fOst) {
                view.setBackgroundResource(R.color.WhiteRed);
            }
        }*/
        return view;
    }
}
