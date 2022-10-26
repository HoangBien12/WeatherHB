package com.hoang.weatherhb.retrofits;


import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.models.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IWeatherServices {
    @GET("forecast?")
    Call<CurrentWeather5day> getWeather5DayByCityName(@Query("lat") double lat,
                                                      @Query("lon") double lon,
                                                      @Query("appid") String appID,
                                                      @Query("lang") String lang);

    @GET("weather?")
    Call<CurrentWeather> getWeatherByCityName(@Query("lat") double lat,
                                              @Query("lon") double lon,
                                              @Query("appid") String appID,
                                              @Query("lang") String lang);

}
