package com.hoang.weatherhb.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.hoang.weatherhb.R;
import com.hoang.weatherhb.listviewadapter.ForecastDetailAdapter;
import com.hoang.weatherhb.model5day.CurrentWeather5day;
import com.hoang.weatherhb.utils.FormatData;

public class ForecastDetail extends BottomSheetDialogFragment {
    private final CurrentWeather5day currentWeather5day;
    private final int i;

    public ForecastDetail(CurrentWeather5day currentWeather5day, int i) {
        this.currentWeather5day = currentWeather5day;
        this.i = i;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_detail, null);
        bottomSheetDialog.setContentView(view);

        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setSkipCollapsed(true);

        ViewPager2 viewPager2 = view.findViewById(R.id.viewPager2);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        TextView tvTime = view.findViewById(R.id.tvTime);
        tvTime.setText(FormatData.date(currentWeather5day.getList().get(i).getDt()));
        ForecastDetailAdapter adapter = new ForecastDetailAdapter(currentWeather5day);
        viewPager2.setAdapter(adapter);
        viewPager2.setCurrentItem(i, false);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> tab.setIcon(getResources().getDrawable(R.drawable.dot))).attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tvTime.setText(FormatData.date(currentWeather5day.getList().get(position).getDt()));
            }
        });

        return bottomSheetDialog;
    }

}
