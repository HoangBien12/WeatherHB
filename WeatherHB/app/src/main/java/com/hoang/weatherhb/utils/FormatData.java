package com.hoang.weatherhb.utils;

import com.hoang.weatherhb.activity.MainActivity;
import com.hoang.weatherhb.saveData.MySharedPreferences;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FormatData {
    public static String temp(double tp) {
        double temp;
        String tem = MySharedPreferences.getTemp(MainActivity.getAppContext());
        DecimalFormat dcf = new DecimalFormat("#");
        if (tem.equals("C")) {
            temp = tp - 273.15;
        } else if (tem.equals("F")) {
            temp = (tp - 273.15) * 1.8000 + 32.00;
        } else {
            temp = tp;
        }
        return dcf.format(temp) + "°";
    }

    public static String date(long tm) {
        String time = "hh:mm";
        Date date = new Date(tm * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EE, dd/MM hh:mm aaa");
        time = dateFormat.format(date);
        return time;
    }

    public static String dateSim(long tm) {
        String time = "hh:mm";
        Date date = new Date(tm * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        time = dateFormat.format(date);
        return time;
    }

    public static String time(long tm) {
        String time = "hh:mm";
        Date date = new Date(tm * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aaa");
        time = dateFormat.format(date);
        return time;
    }

    public static String finalDay(long tm) {
        String time = "hh:mm";
        Date date = new Date(tm * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE");
        time = dateFormat.format(date);
        return time;
    }
}
