package com.example.hikemate.Observation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.hikemate.R;

public class ObservationDetail extends AppCompatActivity {
    public static final String OBSERVATION_KEY = "observation_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_detail);
    }
}