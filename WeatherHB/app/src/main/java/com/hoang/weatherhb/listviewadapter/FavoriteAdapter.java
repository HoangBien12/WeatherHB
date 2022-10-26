package com.hoang.weatherhb.listviewadapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.utils.FormatData;
import com.hoang.weatherhb.utils.Global;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private final ArrayList<WeatherSave> favorite;

    public void setCallback(FavoriteClickListener callback) {
        Callback = callback;
    }

    public FavoriteAdapter(ArrayList<WeatherSave> favorite, FavoriteClickListener callback) {
        this.favorite = favorite;
        Callback = callback;
    }

    private Context mContext;
    private FavoriteClickListener Callback;

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_adapter, parent, false);
        return new FavoriteViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.tvCity.setText(favorite.get(position).getCity());
        holder.tvDate.setText(FormatData.dateSim(favorite.get(position).getDate()));
        holder.tvDescription.setText(favorite.get(position).getDescription());
        holder.tvTemp.setText(FormatData.temp(favorite.get(position).getTemp()));
        Glide.with(mContext).load(Global.IMAGE_ICON_URL(favorite.get(position).getIcon())).into(holder.imgWeather);

    }

    @Override
    public int getItemCount() {
        return favorite.size();
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView tvCity;
        private final TextView tvDate;
        private final TextView tvDescription;
        private final TextView tvTemp;
        private final ImageView imgWeather;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            LinearLayout linearLayout = itemView.findViewById(R.id.linearLayout);
            tvCity = itemView.findViewById(R.id.tvCity);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTemp = itemView.findViewById(R.id.tvTemp);
            imgWeather = itemView.findViewById(R.id.imgWeather);
            linearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (Callback != null) {
                Callback.onItemClickListener(getAdapterPosition());
            }
        }
    }

}
