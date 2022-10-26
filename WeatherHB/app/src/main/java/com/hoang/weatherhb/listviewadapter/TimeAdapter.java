package com.hoang.weatherhb.listviewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.utils.FormatData;
import com.hoang.weatherhb.utils.Global;

public class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder> {
    private final CurrentWeather5day currentWeather5day;
    private Context mContext;

    public TimeAdapter(CurrentWeather5day currentWeather5day) {
        this.currentWeather5day = currentWeather5day;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_adapter, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        holder.tvTime.setText(FormatData.time(currentWeather5day.getList().get(position).getDt()));
        holder.tvTemp.setText(FormatData.temp(currentWeather5day.getList().get(position).getMain().getTemp()));
        Glide.with(mContext).load(Global.IMAGE_ICON_URL(currentWeather5day.getList().get(position).getWeather().get(0).getIcon())).into(holder.imgWeather);
    }

    @Override
    public int getItemCount() {
        return 7;
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTime;
        private final TextView tvTemp;
        private final ImageView imgWeather;

        public TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            imgWeather = itemView.findViewById(R.id.imgWeather);
        }
    }
}