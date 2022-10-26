package com.hoang.weatherhb.listviewadapter;

import java.io.Serializable;

public class WeatherSave implements Serializable {
    private final String city;
    private final long date;
    private final String description;
    private final String icon;
    private final float temp;
    private final double latitude;
    private final double longitude;

    public WeatherSave(String city, long date, String description, String icon, float temp, double latitude, double longitude) {
        this.city = city;
        this.date = date;
        this.description = description;
        this.icon = icon;
        this.temp = temp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public long getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public float getTemp() {
        return temp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
