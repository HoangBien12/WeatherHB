
package com.hoang.weatherhb.model5day;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.hoang.weatherhb.utils.FormatData;

public class CurrentWeather5day {

    @SerializedName("cod")
    @Expose
    private String cod;
    @SerializedName("message")
    @Expose
    private Integer message;
    @SerializedName("cnt")
    @Expose
    private Integer cnt;
    @SerializedName("list")
    @Expose
    private java.util.List<List> list = null;
    @SerializedName("city")
    @Expose
    private City city;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String forecastDetail(int i){
        return "\n\n"+getList().get(i).getClouds().getAll()+"%\n\n"
                +getList().get(i).getMain().getHumidity()+" %\n\n"
                +getList().get(i).getVisibility()+" m\n\n"
                +getList().get(i).getWind().getDeg()+"°\n\n"
                +getList().get(i).getWind().getSpeed()+" m/s\n\n"
                +getList().get(i).getWind().getGust()+" m/s\n\n"
                +FormatData.temp(getList().get(i).getMain().getTempMax())+"\n\n"
                +FormatData.temp(getList().get(i).getMain().getTempMin())+"\n\n"
                +getList().get(i).getMain().getGrndLevel()+" hPa\n\n"
                +getList().get(i).getMain().getSeaLevel()+" hPa\n\n";
    }

}
