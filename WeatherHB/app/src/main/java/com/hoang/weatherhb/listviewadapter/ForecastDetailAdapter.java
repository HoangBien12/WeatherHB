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

public class ForecastDetailAdapter extends RecyclerView.Adapter<ForecastDetailAdapter.ForecastViewHolder> {
    private final CurrentWeather5day currentWeather5day;
    private Context mContext;

    public ForecastDetailAdapter(CurrentWeather5day currentWeather5day) {
        this.currentWeather5day = currentWeather5day;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_forecast, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        Glide.with(mContext).load(Global.IMAGE_ICON_URL(currentWeather5day.getList().get(position).getWeather().get(0).getIcon())).into(holder.imgWeather);
        holder.tvTemp.setText(FormatData.temp(currentWeather5day.getList().get(position).getMain().getTemp()));
        holder.tvFeels_Like.setText(FormatData.temp(currentWeather5day.getList().get(position).getMain().getFeelsLike()));
        holder.tvDescription.setText(currentWeather5day.getList().get(position).getWeather().get(0).getDescription());
        holder.tvDetail.setText(currentWeather5day.forecastDetail(position));
    }

    @Override
    public int getItemCount() {
        return currentWeather5day.getList().size();
    }

    public class ForecastViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTemp;
        private final TextView tvFeels_Like;
        private final TextView tvDescription;
        private final TextView tvDetail;
        private final ImageView imgWeather;
        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            tvFeels_Like = itemView.findViewById(R.id.tvFeels_Like);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDetail = itemView.findViewById(R.id.tvDetail);
        }
    }
}
