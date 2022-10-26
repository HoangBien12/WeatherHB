package com.hoang.weatherhb.listviewadapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.utils.FormatData;
import com.hoang.weatherhb.utils.Global;

public class ForecastAdapter extends BaseAdapter {
    private final CurrentWeather5day currentWeather5day;

    public ForecastAdapter(CurrentWeather5day currentWeather5day) {
        this.currentWeather5day = currentWeather5day;
    }

    @Override
    public int getCount() {
        return currentWeather5day.getList().size();
    }

    @Override
    public Object getItem(int i) {
        return currentWeather5day.getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = View.inflate(viewGroup.getContext(), R.layout.forecast_adapter,null);
        }
        TextView tvTime = view.findViewById(R.id.tvTime);
        TextView tvTemp = view.findViewById(R.id.tvTemp);
        TextView tvFeels_Like = view.findViewById(R.id.tvFeels_Like);
        ImageView imgWeather = view.findViewById(R.id.imgWeather);

        tvTime.setText(FormatData.date(currentWeather5day.getList().get(i).getDt()));
        tvTemp.setText(FormatData.temp(currentWeather5day.getList().get(i).getMain().getTemp()));
        tvFeels_Like.setText("FeelsLike "+FormatData.temp(currentWeather5day.getList().get(i).getMain().getFeelsLike()));
        Glide.with(viewGroup.getContext()).load(Global.IMAGE_ICON_URL(currentWeather5day.getList().get(i).getWeather().get(0).getIcon())).into(imgWeather);

        return view;
    }
}
