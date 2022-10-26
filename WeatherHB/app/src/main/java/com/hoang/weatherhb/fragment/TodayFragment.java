package com.hoang.weatherhb.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.databinding.FragmentTodayBinding;
import com.hoang.weatherhb.listviewadapter.RainAdapter;
import com.hoang.weatherhb.listviewadapter.TimeAdapter;
import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.models.CurrentWeather;
import com.hoang.weatherhb.saveData.MySharedPreferences;
import com.hoang.weatherhb.utils.FormatData;
import com.hoang.weatherhb.utils.Global;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TodayFragment extends Fragment {
    private CurrentWeather currentWeather;
    private CurrentWeather5day currentWeather5day;
    private FragmentTodayBinding todayBinding;
    private TextView tvTemp, tvFeels_Like, tvDayRain;
    private ImageView imgWeather;
    private RecyclerView recyclerView, recyclerRain;
    private BarChart barChart;

    public TodayFragment() {
        // Required empty public constructor
    }

    public static TodayFragment newInstance() {
        TodayFragment fragment = new TodayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        todayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_today, container, false);
        View view = todayBinding.getRoot();
        initView(view);
        initData();
        initData5();
        return view;
    }

    private void initView(View view) {
        imgWeather = view.findViewById(R.id.imgWeather);
        tvTemp = view.findViewById(R.id.tvTemp);
        tvFeels_Like = view.findViewById(R.id.tvFeels_Like);
        recyclerView = view.findViewById(R.id.recyclerView);
        todayBinding.setCurrentWeather(currentWeather);
        barChart = view.findViewById(R.id.barChart);
        recyclerRain = view.findViewById(R.id.recyclerRain);
        tvDayRain = view.findViewById(R.id.tvDayRain);
    }

    private void initData() {
        if (currentWeather != null) {
            tvTemp.setText(FormatData.temp(currentWeather.getMain().getTemp()));
            tvFeels_Like.setText(FormatData.temp(currentWeather.getMain().getFeelsLike()));
            Glide.with(this).load(Global.IMAGE_ICON_URL(currentWeather.getWeather().get(0).getIcon())).into(imgWeather);
        }

    }

    private void initData5() {
        if (currentWeather5day != null) {
            tvDayRain.setVisibility(View.VISIBLE);
            TimeAdapter timeAdapter = new TimeAdapter(currentWeather5day);
            recyclerView.setAdapter(timeAdapter);
            barChartDay();
        }

    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        this.currentWeather = currentWeather;
        todayBinding.setCurrentWeather(currentWeather);
        if (getActivity() != null) {
            initData();
        }

    }

    public void setCurrentWeather5day(CurrentWeather5day currentWeather5day) {
        this.currentWeather5day = currentWeather5day;
        if (getActivity() != null) {
            initData5();
        }

    }

    private void barChartDay() {
        barChart.setFitBars(true);
        barChart.getXAxis().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisRight().setTextColor(Color.WHITE);
        barChart.getLegend().setTextColor(Color.WHITE);
        barChart.getDescription().setTextColor(Color.WHITE);
        barChart.getDescription().setText("Days");
        barChart.animateY(1000);

        ArrayList<BarEntry> visitorsTempMax = new ArrayList<>();
        ArrayList<BarEntry> visitorsTempMin = new ArrayList<>();
        ArrayList<BarEntry> visitorsRain = new ArrayList<>();
        int day = date(currentWeather5day.getList().get(0).getDt());
        int tempMax = temp(currentWeather5day.getList().get(0).getMain().getTemp());
        int tempMin = tempMax;
        int rain = 0;
        int j = 0;
        ArrayList<String> stringDay = new ArrayList<>();
        for (int i = 0; i < currentWeather5day.getList().size(); i++) {
            int days = date(currentWeather5day.getList().get(i).getDt());
            int temps = temp(currentWeather5day.getList().get(i).getMain().getTemp());
            String rains = currentWeather5day.getList().get(i).getWeather().get(0).getDescription();
            if (day != days || i == currentWeather5day.getList().size() - 1) {
                visitorsTempMax.add(new BarEntry(j, tempMax));
                visitorsTempMin.add(new BarEntry(j, tempMin));
                visitorsRain.add(new BarEntry(currentWeather5day.getList().get(i - 1).getDt(), (rain * 12.5f)));
                stringDay.add(String.valueOf(day));
                day = days;
                tempMax = 0;
                tempMin = temps;
                rain = 0;
                j++;
            }
            if (tempMax < temps) tempMax = temps;
            if (tempMin > temps) tempMin = temps;
            if (rains.toLowerCase().contains("rain") || rains.toLowerCase().contains("mưa")) rain++;
        }

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        float dpWidth = outMetrics.widthPixels / density;
        int columns = Math.round((dpWidth - 20) / 110);

        RainAdapter rainAdapter = new RainAdapter(visitorsRain);
        recyclerRain.setLayoutManager(new GridLayoutManager(getActivity(), columns));
        recyclerRain.setAdapter(rainAdapter);


        BarDataSet barDataSet = new BarDataSet(visitorsTempMax, "Temp max");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(16f);

        BarDataSet barDataSet1 = new BarDataSet(visitorsTempMin, "Temp min");
        barDataSet1.setColor(Color.rgb(3, 218, 197));
        barDataSet1.setValueTextColor(Color.WHITE);
        barDataSet1.setValueTextSize(16f);

        BarData barData = new BarData(barDataSet, barDataSet1);
        barData.setValueFormatter(new IntegerFormatter());

        barChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return stringDay.get((int) value);
            }
        });

        barChart.setTouchEnabled(false);
        barChart.setData(barData);

    }

    public static int temp(double tp) {
        double temp;
        String tem = MySharedPreferences.getTemp(newInstance().getContext());
        DecimalFormat dcf = new DecimalFormat("#");
        if (tem.equals("C")) {
            temp = tp - 273.15;
        } else if (tem.equals("F")) {
            temp = (tp - 273.15) * 1.8000 + 32.00;
        } else {
            temp = tp;
        }
        return Integer.parseInt(dcf.format(temp));

    }

    public static int date(long tm) {
        Date date = new Date(tm * 1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return Integer.parseInt(dateFormat.format(date));
    }

    public static class IntegerFormatter extends ValueFormatter {
        private final DecimalFormat mFormat;

        public IntegerFormatter() {
            mFormat = new DecimalFormat("###,##0");
        }

        @Override
        public String getBarLabel(BarEntry barEntry) {
            return mFormat.format(barEntry.getY()) + "°";
        }
    }

}