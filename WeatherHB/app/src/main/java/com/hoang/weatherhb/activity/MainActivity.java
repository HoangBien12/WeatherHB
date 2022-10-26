package com.hoang.weatherhb.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.fragment.ForecastFragment;
import com.hoang.weatherhb.fragment.TodayFragment;
import com.hoang.weatherhb.listviewadapter.WeatherSave;
import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.models.CurrentWeather;
import com.hoang.weatherhb.retrofits.IWeatherServices;
import com.hoang.weatherhb.retrofits.RetrofitClient;
import com.hoang.weatherhb.saveData.MySharedPreferences;
import com.hoang.weatherhb.utils.Global;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private BottomNavigationView bottomNavigationView;
    private TodayFragment todayFragment;
    private ForecastFragment dayFragment;
    private TextView tvCityName;
    private ImageView imgMenu;
    private CurrentWeather currentWeather;
    private CurrentWeather5day currentWeather5day;
    private static Context mContext;
    private String lang, userCity;
    private double latitude, longitude, latFlag, longFlag;
    private Location gps_loc, network_loc;
    private LocationManager locationManager;
    private ArrayList<WeatherSave> favorite;
    private File file;
    private TextView tvNoInternet;
    private SwipeRefreshLayout swipeRefresh;
    private int mCurrentFragment = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setting();
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mContext = getApplicationContext();

        initView();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    private void initData() {
        getLocation();
        bottomNavigation();
        tvCityName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });

    }

    private void initView() {
        todayFragment = TodayFragment.newInstance();
        dayFragment = ForecastFragment.newInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        tvCityName = findViewById(R.id.tvCityName);
        imgMenu = findViewById(R.id.imgMenu);
        tvNoInternet = findViewById(R.id.tvNoInternet);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeColors(Color.RED);
        swipeRefresh.setProgressViewOffset(true, 0, 150);
        swipeRefresh.setRefreshing(true);
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(this, imgMenu);
        popupMenu.getMenuInflater().inflate(R.menu.menu_weather, popupMenu.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_favorite:
                        if (currentWeather != null) {
                            file = new File(MainActivity.this.getFilesDir(), "favorite");
                            if (file.exists()) {
                                favorite = (ArrayList<WeatherSave>) MySharedPreferences.readFile(file);
                                Collections.reverse(favorite);
                            } else {
                                favorite = new ArrayList<>();
                            }

                            WeatherSave save = new WeatherSave(userCity, currentWeather.getDt(),
                                    currentWeather.getWeather().get(0).getDescription(),
                                    currentWeather.getWeather().get(0).getIcon(), currentWeather.getMain().getTemp(),
                                    latitude, longitude);

                            if (favorite.size() == 0) {
                                favorite.add(save);
                            } else {
                                for (int i = 0; i < favorite.size(); i++) {
                                    if (favorite.get(i).getCity().equals(userCity)) {
                                        favorite.set(i, save);
                                        break;
                                    } else if (i == favorite.size() - 1) {
                                        favorite.add(save);
                                    }
                                }
                            }

                            Collections.reverse(favorite);
                            MySharedPreferences.saveFile(favorite, file);
                            Toast.makeText(MainActivity.this, R.string.add_favorite_successful, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.item_search:
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.item_setting:
                        Intent intent1 = new Intent(mContext, SettingActivity.class);
                        startActivity(intent1);
                        break;
                    case R.id.item_exit:
                        finishAndRemoveTask();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void bottomNavigation() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, todayFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_today:
                        if (mCurrentFragment != 1) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout, todayFragment).commit();
                            mCurrentFragment = 1;
                        }
                        break;

                    case R.id.menu_day:
                        if (mCurrentFragment != 2) {
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frameLayout, dayFragment).commit();
                            mCurrentFragment = 2;
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void loadDataWeather() {
        IWeatherServices iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByCityName(latitude, longitude, Global.APP_ID, lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.code() == 200) {
                    new LoadCityName().execute();
                    currentWeather = response.body();
                    todayFragment.setCurrentWeather(currentWeather);
                    latFlag = latitude;
                    longFlag = longitude;
                }
                swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Log.d("TAG", "onFailure Weather: " + t.getMessage());
                swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, R.string.load_error, Toast.LENGTH_SHORT).show();

            }
        });
        iWeatherServices.getWeather5DayByCityName(latitude, longitude, Global.APP_ID, lang).enqueue(new Callback<CurrentWeather5day>() {
            @Override
            public void onResponse(Call<CurrentWeather5day> call, Response<CurrentWeather5day> response) {
                if (response.code() == 200) {
                    currentWeather5day = response.body();
                    todayFragment.setCurrentWeather5day(currentWeather5day);
                    dayFragment.setCurrentWeather5day(currentWeather5day);
                }
            }

            @Override
            public void onFailure(Call<CurrentWeather5day> call, Throwable t) {
                Log.d("TAG", "onFailure 5 Day: " + t.getMessage());

            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        try {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_loc != null) {
            MySharedPreferences.setLatitude(mContext, (float) gps_loc.getLatitude());
            MySharedPreferences.setLongitude(mContext, (float) gps_loc.getLongitude());
        } else if (network_loc != null) {
            MySharedPreferences.setLatitude(mContext, (float) network_loc.getLatitude());
            MySharedPreferences.setLongitude(mContext, (float) network_loc.getLongitude());
        } else {
            boolean gps_enabled = false;
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled && isNetworkAvailable()) {
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, mLocationListener, Looper.getMainLooper());
            }
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            MySharedPreferences.setLatitude(mContext, (float) latitude);
            MySharedPreferences.setLongitude(mContext, (float) longitude);
            loadDataWeather();
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        Resources resources = newBase.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        configuration.uiMode = Configuration.UI_MODE_NIGHT_UNDEFINED;
        Context context = newBase.createConfigurationContext(configuration);

        lang = MySharedPreferences.getLanguage(context);
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        super.attachBaseContext(newBase);
    }

    @Override
    public void onRefresh() {
        loadDataWeather();
    }

    private final BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            latitude = MySharedPreferences.getLatitude(mContext);
            longitude = MySharedPreferences.getLongitude(mContext);
            if (isNetworkAvailable()) {
                tvNoInternet.setVisibility(View.GONE);
                swipeRefresh.setEnabled(true);
                if (latFlag != latitude || longFlag != longitude) {
                    loadDataWeather();
                } else if (currentWeather != null && currentWeather5day != null) {
                    todayFragment.setCurrentWeather(currentWeather);
                    todayFragment.setCurrentWeather5day(currentWeather5day);
                    dayFragment.setCurrentWeather5day(currentWeather5day);
                }
            } else {
                if (currentWeather == null || currentWeather5day == null) {
                    swipeRefresh.setEnabled(false);
                    tvNoInternet.setVisibility(View.VISIBLE);
                } else {
                    if (latFlag != latitude || longFlag != longitude) {
                        Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
                        MySharedPreferences.setLatitude(mContext, (float) latFlag);
                        MySharedPreferences.setLongitude(mContext, (float) longFlag);
                    } else {
                        todayFragment.setCurrentWeather(currentWeather);
                        todayFragment.setCurrentWeather5day(currentWeather5day);
                        dayFragment.setCurrentWeather5day(currentWeather5day);
                    }
                }

            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setting() {
        String theme = MySharedPreferences.getTheme(this);
        if (theme.equals("Black")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (theme.equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

    private class LoadCityName extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    userCity = addresses.get(0).getAdminArea();
                    if (userCity == null) {
                        userCity = addresses.get(0).getLocality();
                    }
                    if (userCity == null) {
                        userCity = addresses.get(0).getCountryName();
                    }
                } else {
                    userCity = "Unknown";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userCity;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvCityName.setText(s);
            MySharedPreferences.setCity(mContext, s);
        }
    }

    public static Context getAppContext() {
        return mContext;
    }

}