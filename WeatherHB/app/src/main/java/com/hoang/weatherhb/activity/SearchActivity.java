package com.hoang.weatherhb.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.listviewadapter.FavoriteAdapter;
import com.hoang.weatherhb.listviewadapter.FavoriteClickListener;
import com.hoang.weatherhb.listviewadapter.WeatherSave;
import com.hoang.weatherhb.models.CurrentWeather;
import com.hoang.weatherhb.retrofits.IWeatherServices;
import com.hoang.weatherhb.retrofits.RetrofitClient;
import com.hoang.weatherhb.saveData.MySharedPreferences;
import com.hoang.weatherhb.utils.FormatData;
import com.hoang.weatherhb.utils.Global;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements FavoriteClickListener {
    private EditText edEnterCityName;
    private TextView tvCityName, tvCity, tvDescription, tvTemp;
    private TextView tvLocation, tvCan, tvFavorite;
    private ImageView imgWeather;
    private boolean flag = true;
    private boolean enter = true;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private Location gps_loc;
    private Location network_loc;
    private double longitude, latitude;
    private CurrentWeather currentWeather;
    private LocationManager locationManager;
    private ArrayList<WeatherSave> favorite;
    private FavoriteAdapter adapter;
    ItemTouchHelper itemTouchHelper;
    private ProgressBar progress, progressBar;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        initView();
        allowLocation();
        loadRecyclerView();

        edEnterCityName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (enter) {
                        if (edEnterCityName.getText().toString().trim().length() > 0) {
                            enter = false;
                            progress.setVisibility(View.VISIBLE);
                            new EnterCityName().execute();
                        }

                    }
                    handled = true;
                }
                return handled;
            }
        });

        edEnterCityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                tvCan.setVisibility(View.VISIBLE);
                tvFavorite.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
        tvCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeKeyboard();
                tvCan.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (favorite.size() > 0) {
                    tvFavorite.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    private void initView() {
        edEnterCityName = findViewById(R.id.edEnterCityName);
        tvCityName = findViewById(R.id.tvCityName);
        linearLayout = findViewById(R.id.linearLayout);
        tvCity = findViewById(R.id.tvCity);
        tvTemp = findViewById(R.id.tvTemp);
        tvLocation = findViewById(R.id.tvLocation);
        tvCan = findViewById(R.id.tvCan);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        tvFavorite = findViewById(R.id.tvFavorite);
        recyclerView = findViewById(R.id.recyclerView);
        imgWeather = findViewById(R.id.imgWeather);
        tvDescription = findViewById(R.id.tvDescription);
        tvCityName.setText(MySharedPreferences.getCity(this));
    }

    private void loadRecyclerView() {
        File file = new File(this.getFilesDir(), "favorite");
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                favorite.remove(viewHolder.getAdapterPosition());
                MySharedPreferences.saveFile(favorite, file);
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        favorite = new ArrayList<>();
        if (file.exists()) {
            favorite = (ArrayList<WeatherSave>) MySharedPreferences.readFile(file);
        }
        if (favorite.size() > 0) {
            adapter = new FavoriteAdapter(favorite, this);
            tvFavorite.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void allowLocation() {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                boolean gps_enabled = false;
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (flag) {
                        tvLocation.setText(R.string.location_access);
                        flag = false;
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                } else if (!gps_enabled) {
                    switchGPS_ON();
                } else {
                    loadDataLocation();
                }

            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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

        Location final_loc;
        if (gps_loc != null) {
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else if (network_loc != null) {
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        } else {
            return;
        }
        tvLocation.setText(R.string.my_location);
        if (isNetworkAvailable()) loadDataWeather();
        new GetCityName().execute();
    }

    private void loadDataWeather() {
        String lang = MySharedPreferences.getLanguage(this);
        IWeatherServices iWeatherServices = RetrofitClient.createServices(IWeatherServices.class);
        iWeatherServices.getWeatherByCityName(latitude, longitude, Global.APP_ID, lang).enqueue(new Callback<CurrentWeather>() {
            @Override
            public void onResponse(Call<CurrentWeather> call, Response<CurrentWeather> response) {
                if (response.code() == 200) {
                    currentWeather = response.body();
                    tvDescription.setText(currentWeather.getWeather().get(0).getDescription());
                    tvTemp.setText(FormatData.temp(currentWeather.getMain().getTemp()));
                    Glide.with(getApplicationContext()).load(Global.IMAGE_ICON_URL(currentWeather.getWeather().get(0).getIcon())).into(imgWeather);
                }

            }

            @Override
            public void onFailure(Call<CurrentWeather> call, Throwable t) {
                Log.d("TAG", "onFailure Weather: " + t.getMessage());
            }
        });

    }

    private void switchGPS_ON() {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // gps on
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // gps off
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        resolvableApiException.startResolutionForResult(SearchActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendIntentException) {
                        sendIntentException.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case SearchActivity.RESULT_OK: {
                    loadDataLocation();
                    break;
                }
                case SearchActivity.RESULT_CANCELED: {
                    Toast.makeText(this, "Location not enabled", Toast.LENGTH_SHORT).show();
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadDataLocation() {
        if (longitude == 0) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 500, mLocationListener, Looper.getMainLooper());
            tvLocation.setText("Loading");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            MySharedPreferences.setLatitude(SearchActivity.this, (float) latitude);
            MySharedPreferences.setLongitude(SearchActivity.this, (float) longitude);
            finish();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            MySharedPreferences.setLatitude(SearchActivity.this, (float) latitude);
            MySharedPreferences.setLongitude(SearchActivity.this, (float) longitude);
            locationManager.removeUpdates(mLocationListener);
            progressBar.setVisibility(View.GONE);
            getLocation();
        }
    };

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            view.clearFocus();
        }
    }

    @Override
    public void onItemClickListener(int position) {
        MySharedPreferences.setLatitude(SearchActivity.this, (float) favorite.get(position).getLatitude());
        MySharedPreferences.setLongitude(SearchActivity.this, (float) favorite.get(position).getLongitude());
        finish();
    }

    private class EnterCityName extends AsyncTask<Void, Void, Byte> {

        @Override
        protected Byte doInBackground(Void... voids) {
            byte result = 0;
            if (isNetworkAvailable()) {
                Geocoder geocoder = new Geocoder(SearchActivity.this);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(edEnterCityName.getText().toString().trim(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        MySharedPreferences.setLongitude(SearchActivity.this, (float) addresses.get(0).getLongitude());
                        MySharedPreferences.setLatitude(SearchActivity.this, (float) addresses.get(0).getLatitude());
                        result = 2;
                        finish();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                result = 1;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Byte aByte) {
            super.onPostExecute(aByte);
            enter = true;
            progress.setVisibility(View.INVISIBLE);
            if (aByte == 0) {
                Toast.makeText(SearchActivity.this, R.string.wrong_city_name, Toast.LENGTH_SHORT).show();
            } else if (aByte == 1) {
                Toast.makeText(SearchActivity.this, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetCityName extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            String subAdminArea, cityName;
            try {
                Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    subAdminArea = addresses.get(0).getSubAdminArea();
                    cityName = addresses.get(0).getAdminArea();
                    if (cityName == null) {
                        cityName = addresses.get(0).getLocality();
                    }
                    if (cityName == null) {
                        cityName = addresses.get(0).getCountryName();
                    }
                    if (subAdminArea != null) {
                        cityName = subAdminArea + ", " + cityName;
                    }
                } else cityName = "Unknown";

            } catch (Exception e) {
                cityName = "---";
                e.printStackTrace();
            }
            return cityName;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tvCity.setText(s);
        }
    }

}