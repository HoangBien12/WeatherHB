package com.hoang.weatherhb.saveData;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MySharedPreferences {
    private static SharedPreferences sharedPreferences;
    private static final String SHARE_NAME = "WEATHER";
    private static final String SHARE_LANG = "LANG";
    private static final String SHARE_TEMP = "TEMP";
    private static final String SHARE_CITY = "CITY";
    private static final String SHARE_THEME = "THEME";
    private static final String SHARE_LATITUDE = "LATITUDE";
    private static final String SHARE_LONGITUDE = "LONGITUDE";

    public static SharedPreferences getInstance(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static String getLanguage(Context context) {
        return getInstance(context).getString(SHARE_LANG, "vi");
    }

    public static void setLanguage(Context context, String lang) {
        getInstance(context).edit().putString(SHARE_LANG, lang).apply();
    }

    public static String getTemp(Context context) {
        return getInstance(context).getString(SHARE_TEMP, "C");
    }

    public static void setTemp(Context context, String temp) {
        getInstance(context).edit().putString(SHARE_TEMP, temp).apply();
    }

    public static float getLatitude(Context context) {
        return getInstance(context).getFloat(SHARE_LATITUDE, 21.0245f);
    }

    public static void setLatitude(Context context, float lat) {
        getInstance(context).edit().putFloat(SHARE_LATITUDE, lat).apply();
    }

    public static float getLongitude(Context context) {
        return getInstance(context).getFloat(SHARE_LONGITUDE, 105.8412f);
    }

    public static void setLongitude(Context context, float lat) {
        getInstance(context).edit().putFloat(SHARE_LONGITUDE, lat).apply();
    }

    public static String getCity(Context context) {
        return getInstance(context).getString(SHARE_CITY, "Hà Nội");
    }

    public static void setCity(Context context, String city) {
        getInstance(context).edit().putString(SHARE_CITY, city).apply();
    }

    public static String getTheme(Context context) {
        return getInstance(context).getString(SHARE_THEME, "Auto");
    }

    public static void setTheme(Context context, String theme) {
        getInstance(context).edit().putString(SHARE_THEME, theme).apply();
    }

    public static void saveFile(Object data, File path) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(data);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Object readFile(File path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object data = ois.readObject();
            ois.close();
            fis.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
