package com.example.hikemate;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Weather;
import com.example.hikemate.WeatherForecast.CalendarUtils;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.google.android.material.card.MaterialCardView;

public class MainFragment extends Fragment implements NetworkUtils.OnConnectivityChangeListener{
    private MaterialCardView weatherCard;
    private NetworkUtils networkUtils;
    private TextView txtEmpty, txtDate, txtTemp, txtWind, txtHumidity, txtWeatherRecommend, txtWeatherDes, updateWeather;

    private LinearLayout weatherLayout, lostLayout;
    private HikeDatabase db;
    private ImageView imageWeather;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        initView(view);
        // Create an instance of NetworkConnectivityMonitor and register the listener
        networkUtils = new NetworkUtils(getActivity());
        networkUtils.setListener(this);
        networkUtils.register();
        initListener();

        initWeatherForecast();

        return view;
    }

    private void initListener() {
        weatherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WeatherActivity.class));
            }
        });
    }

    private void initWeatherForecast() {
        if (networkUtils.isConnected()) {
            weatherLayout.setVisibility(View.VISIBLE);
            lostLayout.setVisibility(View.GONE);
            Weather weather = db.weatherDao().getAll();
            if (weather != null && weather.getHumidity() > 0) {
                updateWeather.setVisibility(View.GONE);
                txtDate.setText(CalendarUtils.formattedDayOfWeek(weather.getDate()));
                int valueTemp = (int) weather.getTemp();
                if (weather.getUnit().equals("°F")) {
                    valueTemp = (valueTemp * 9 / 5) + 32;
                }
                String temp = valueTemp + weather.getUnit();
                txtTemp.setText(temp);
                txtWind.setText(weather.getSpeed() + " m/s");
                txtHumidity.setText(weather.getHumidity() + "%");
                txtWeatherDes.setText(WeatherActivity.capitalizeFirstLetter(weather.getDescription()));
                switch (weather.getMain()) {
                    case "Clear":
                        imageWeather.setImageResource(R.drawable.sun);
                        break;
                    case "Clouds":
                        if(GetCurrentLanguage.getCurrentLanguage(getActivity()).equals("en")) {
                            switch (weather.getDescription()) {
                                case "few clouds":
                                    imageWeather.setImageResource(R.drawable.cloudy_sunny);
                                    break;
                                case "scattered clouds":
                                    imageWeather.setImageResource(R.drawable.cloudy);
                                    break;
                                case "broken clouds":
                                case "overcast clouds":
                                    imageWeather.setImageResource(R.drawable.cloudy_3);
                                    break;
                            }
                        } else {
                            switch (weather.getDescription()) {
                                case "mây thưa":
                                    imageWeather.setImageResource(R.drawable.cloudy_sunny);
                                    break;
                                case "mây rải rác":
                                    imageWeather.setImageResource(R.drawable.cloudy);
                                    break;
                                case "mây cụm":
                                case "mây đen u ám":
                                    imageWeather.setImageResource(R.drawable.cloudy_3);
                                    break;
                            }
                        }
                        break;
                    case "Rain":
                        imageWeather.setImageResource(R.drawable.rainy);
                        break;
                    case "Thunderstorm":
                        imageWeather.setImageResource(R.drawable.storm);
                        break;
                    case "Snow":
                        imageWeather.setImageResource(R.drawable.snowy);
                        break;
                }
                String weatherRecommend = "";
                if (weather.getMain().equals("Rain") || weather.getMain().equals("Thunderstorm")) {
                    if (weather.getHumidity() > 80 || weather.getSpeed() > 15) {
                        weatherRecommend = "Do something";
                        txtWeatherRecommend.setText(weatherRecommend);
                    } else {
                        weatherRecommend = "Do everything";
                        txtWeatherRecommend.setText(weatherRecommend);
                    }
                } else {
                    weatherRecommend = "Nothing";
                    txtWeatherRecommend.setText(weatherRecommend);
                }
            }
            else {
                updateWeather.setVisibility(View.VISIBLE);
            }
        }
        else {
            weatherLayout.setVisibility(View.GONE);
            lostLayout.setVisibility(View.VISIBLE);
            weatherCard.setClickable(false);
        }
    }

    private void initView(View view) {
        txtEmpty = view.findViewById(R.id.txtEmpty);
        weatherLayout = view.findViewById(R.id.weatherLayout);
        lostLayout = view.findViewById(R.id.lostLayout);
        txtDate = view.findViewById(R.id.txtDate);
        txtTemp = view.findViewById(R.id.txtTemp);
        txtWind = view.findViewById(R.id.txtWind);
        txtHumidity = view.findViewById(R.id.txtHumidity);
        txtWeatherRecommend = view.findViewById(R.id.txtWeatherRecommend);
        txtWeatherDes = view.findViewById(R.id.txtWeatherDes);
        updateWeather = view.findViewById(R.id.updateWeather);
        weatherCard = view.findViewById(R.id.weatherCard);
        imageWeather = view.findViewById(R.id.imageWeather);
        db = HikeDatabase.getInstance(getActivity());
    }

    @Override
    public void onConnectivityChanged(boolean isConnected) {
        if(isConnected){
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initWeatherForecast();
                    weatherCard.setClickable(true);
                }
            });
        } else {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    weatherLayout.setVisibility(View.GONE);
                    lostLayout.setVisibility(View.VISIBLE);
                    weatherCard.setClickable(false);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        networkUtils.unregister();
    }

    @Override
    public void onResume() {
        super.onResume();
        initWeatherForecast();
    }
}
