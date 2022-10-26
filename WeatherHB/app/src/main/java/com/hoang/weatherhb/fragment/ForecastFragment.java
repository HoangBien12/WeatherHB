package com.hoang.weatherhb.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hoang.weatherhb.R;
import com.hoang.weatherhb.listviewadapter.ForecastAdapter;
import com.hoang.weatherhb.model5day.CurrentWeather5day;

public class ForecastFragment extends Fragment {
    private CurrentWeather5day currentWeather5day;
    private ListView listViewDay;

    public ForecastFragment() {
        // Required empty public constructor
    }

    public static ForecastFragment newInstance() {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);
        listViewDay = view.findViewById(R.id.listViewDay);
        listViewDay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialogForecast(currentWeather5day, i);
            }
        });
        initData();
        return view;
    }

    private void initData() {
        if (currentWeather5day != null) {
            ForecastAdapter dayAdapter = new ForecastAdapter(currentWeather5day);
            listViewDay.setAdapter(dayAdapter);
        }

    }

    public void setCurrentWeather5day(CurrentWeather5day currentWeather5day) {
        this.currentWeather5day = currentWeather5day;
        if (getActivity() != null) {
            initData();
        }
    }

    private void dialogForecast(CurrentWeather5day currentWeather5day, int i) {
        ForecastDetail bottomSheetDialogFragment = new ForecastDetail(currentWeather5day, i);
        bottomSheetDialogFragment.show(getActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

}