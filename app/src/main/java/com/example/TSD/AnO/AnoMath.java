package com.example.TSD.AnO;

public class AnoMath {

    public static float round(float value, int prec) {
        float coef = (float)Math.pow(10,prec);
        float result = Math.round(value*coef);
        result = (float) (result/coef);
        return(result);
    };

}
