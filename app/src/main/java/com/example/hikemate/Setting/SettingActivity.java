package com.example.hikemate.Setting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hikemate.Other.ContextWrapper;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Setting;
import com.example.hikemate.Database.Model.Weather;
import com.example.hikemate.MainActivity;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {

    private MaterialToolbar toolbarSetting;
    private TextInputLayout languageDropdown;
    private AutoCompleteTextView languageAutoCompleteTextView;
    private TextView txtUnitWeather;
    private LinearLayout layoutWeather;
    private HikeDatabase db;
    private Setting setting;
    private String unitTemp = "";
    private Weather weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();


        initView();

        initListener();

        initDropDownMenu();
    }

    private void initDropDownMenu() {
        ArrayList<String> customArray = new ArrayList<>();
        customArray.add("Tiếng Việt");
        customArray.add("English");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_menu, customArray);
        languageAutoCompleteTextView.setAdapter(adapter);

        languageAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String language = (String) parent.getItemAtPosition(position);
                if (language.equals("English")) {
                    restart();
                    setting.setLanguage("en");
                    db.settingDao().update(setting);
                } else if (language.equals("Tiếng Việt")) {
                    restart();
                    setting.setLanguage("vi");
                    db.settingDao().update(setting);
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        HikeDatabase db = HikeDatabase.getInstance(this);
        String lng = db.settingDao().getSetting().getLanguage();
        Locale locale;

        locale = new Locale(lng);
        Locale.setDefault(locale);

        Context context = ContextWrapper.wrap(newBase, locale);
        super.attachBaseContext(context);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingActivity.this, MainActivity.class));
    }

    private void restart() {
        startActivity(new Intent(this, SettingActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void initListener() {
        toolbarSetting.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (weather.getUnit().equals("°C")) {
            String celsius = "Celsius (°C)";
            txtUnitWeather.setText(celsius);
        } else if (weather.getUnit().equals("°F")) {
            String fahrenheit = "Fahrenheit (°F)";
            txtUnitWeather.setText(fahrenheit);
        }
        layoutWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTemperatureUnitDialog();
            }
        });
    }

    private void showTemperatureUnitDialog() {
        // List of temperature unit options to display in the dialog
        final String[] temperatureUnits = {"Celsius (°C)", "Fahrenheit (°F)"};
        // Get the current selected unit (you can retrieve this from shared preferences if available)
        int selectedUnitIndex = 2; // Default to Celsius
        if (unitTemp.equals("°C")) {
            selectedUnitIndex = 0;
            String celsius = "Celsius (°C)";
            txtUnitWeather.setText(celsius);
        } else if (unitTemp.equals("°F")) {
            selectedUnitIndex = 1;
            String fahrenheit = "Fahrenheit (°F)";
            txtUnitWeather.setText(fahrenheit);
        }
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog2);
        dialogBuilder.setTitle(getString(R.string.chose_temperature_unit))
                .setSingleChoiceItems(temperatureUnits, selectedUnitIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the user's selection
                        switch (which) {
                            case 0: // Celsius selected
                                unitTemp = "°C";
                                saveTemperatureUnit(unitTemp);
                                String celsius = "Celsius (°C)";
                                txtUnitWeather.setText(celsius);
                                break;
                            case 1: // Fahrenheit selected
                                unitTemp = "°F";
                                saveTemperatureUnit(unitTemp);
                                String fahrenheit = "Fahrenheit (°F)";
                                txtUnitWeather.setText(fahrenheit);
                                break;
                        }
                        // Dismiss the dialog after the user's selection
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", null); // No action needed for OK button

        // Show the dialog
        dialogBuilder.show();
    }

    private void saveTemperatureUnit(String unitTemp) {
        weather = db.weatherDao().getAll();
        weather.setUnit(unitTemp);
        db.weatherDao().updateWeather(weather);
    }

    private void initView() {
        toolbarSetting = findViewById(R.id.toolbarSetting);
        languageDropdown = findViewById(R.id.languageDropdown);
        languageAutoCompleteTextView = findViewById(R.id.languageAutoCompleteTextView);
        txtUnitWeather = findViewById(R.id.txtUnitWeather);
        layoutWeather = findViewById(R.id.layoutWeather);
        db = HikeDatabase.getInstance(this);
        setting = db.settingDao().getSetting();
        weather = db.weatherDao().getAll();
        if (weather == null) {
            weather = new Weather();
            weather.setUnit("°C");
            db.weatherDao().insert(weather);
        }
        unitTemp = weather.getUnit();
    }
}