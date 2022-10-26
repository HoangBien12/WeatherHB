package com.hoang.weatherhb.listviewadapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.BarEntry;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.utils.FormatData;

import java.util.ArrayList;

public class RainAdapter extends RecyclerView.Adapter<RainAdapter.RainViewHolder> {
    ArrayList<BarEntry> visitorsRain;

    public RainAdapter(ArrayList<BarEntry> visitorsRain) {
        this.visitorsRain = visitorsRain;
    }

    @NonNull
    @Override
    public RainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rain_adapter, parent, false);
        return new RainViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RainViewHolder holder, int position) {
        holder.tvDayRain.setText(FormatData.finalDay((long) visitorsRain.get(position).getX()));
        holder.tvProbabilityRain.setText((int) visitorsRain.get(position).getY()+"%");
        holder.progressRain.setProgress((int) visitorsRain.get(position).getY());
    }

    @Override
    public int getItemCount() {
        return visitorsRain.size();
    }

    public class RainViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDayRain;
        private final TextView tvProbabilityRain;
        private final ProgressBar progressRain;
        public RainViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayRain = itemView.findViewById(R.id.tvDayRain);
            tvProbabilityRain = itemView.findViewById(R.id.tvProbabilityRain);
            progressRain = itemView.findViewById(R.id.progressRain);
        }
    }
}
