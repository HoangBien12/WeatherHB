package com.hoang.weatherhb.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.hoang.weatherhb.R;
import com.hoang.weatherhb.saveData.MySharedPreferences;

public class SettingActivity extends AppCompatActivity {
    private RadioGroup radioGroupTemp, radioGroupTheme;
    private RadioButton rdTemC, rdTemF, rdTemK;
    private RadioButton rdThemeAuto, rdThemeBlack, rdThemeLight;
    private Spinner spinnerLang;
    private LinearLayout linearLang;
    private TextView tvLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        radioGroupThemeListen();
        radioGroupTempListen();
        spinnerLangListen();

    }

    private void initView() {
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        radioGroupTemp = findViewById(R.id.radioGroupTemp);
        rdThemeAuto = findViewById(R.id.rdThemeAuto);
        rdThemeBlack = findViewById(R.id.rdThemeBlack);
        rdThemeLight = findViewById(R.id.rdThemeLight);
        rdTemC = findViewById(R.id.rdTemC);
        rdTemF = findViewById(R.id.rdTemF);
        rdTemK = findViewById(R.id.rdTemK);
        spinnerLang = findViewById(R.id.spinnerLang);
        linearLang = findViewById(R.id.linearLang);
        tvLang = findViewById(R.id.tvLang);
    }

    private void radioGroupThemeListen() {
        String theme = MySharedPreferences.getTheme(SettingActivity.this);
        if (theme.equals("Auto")) {
            rdThemeAuto.setChecked(true);
        } else if (theme.equals("Black")) {
            rdThemeBlack.setChecked(true);
        } else {
            rdThemeLight.setChecked(true);
        }

        radioGroupTheme.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroupTheme.getCheckedRadioButtonId()) {
                    case R.id.rdThemeAuto:
                        MySharedPreferences.setTheme(SettingActivity.this, "Auto");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                    case R.id.rdThemeBlack:
                        MySharedPreferences.setTheme(SettingActivity.this, "Black");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case R.id.rdThemeLight:
                        MySharedPreferences.setTheme(SettingActivity.this, "Light");
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                }
            }
        });
    }

    private void radioGroupTempListen() {
        String temp = MySharedPreferences.getTemp(this);
        if (temp.equals("C")) {
            rdTemC.setChecked(true);
        } else if (temp.equals("F")) {
            rdTemF.setChecked(true);
        } else {
            rdTemK.setChecked(true);
        }
        radioGroupTemp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()) {
                    case R.id.rdTemC:
                        MySharedPreferences.setTemp(SettingActivity.this, "C");
                        break;
                    case R.id.rdTemF:
                        MySharedPreferences.setTemp(SettingActivity.this, "F");
                        break;
                    case R.id.rdTemK:
                        MySharedPreferences.setTemp(SettingActivity.this, "K");
                        break;
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void spinnerLangListen() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.lang,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLang.setAdapter(adapter);

        String lang = MySharedPreferences.getLanguage(this);
        if (lang.equals("vi")) {
            tvLang.setText("Việt Nam");
            spinnerLang.setSelection(0, false);
        } else {
            tvLang.setText("English");
            spinnerLang.setSelection(1, false);
        }

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        MySharedPreferences.setLanguage(SettingActivity.this, "vi");
                        reloadActivity();
                        break;
                    case 1:
                        MySharedPreferences.setLanguage(SettingActivity.this, "en");
                        reloadActivity();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        linearLang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerLang.performClick();
            }
        });

    }

    private void reloadActivity() {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finishAffinity();
        startActivity(intent);
    }

}