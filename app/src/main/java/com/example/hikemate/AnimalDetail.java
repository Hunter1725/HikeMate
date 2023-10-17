package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Maps.MapsActivity;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.material.appbar.MaterialToolbar;

public class AnimalDetail extends AppCompatActivity {
    public static final String ANIMAL_KEY = "animal_key";
    private Animal incomingAnimal;
    private MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_animal_detail);
        getSupportActionBar().hide();

        initView();
        initListener();

        ImageView imageView = findViewById(R.id.imageView4);
        TextView nameTextView = findViewById(R.id.animalNameTextView2);
        TextView dangerTextView = findViewById(R.id.animalNameTextView4);
        TextView descriptionTextView = findViewById(R.id.animalAdditionalInfoTextView);
        TextView respondTextView = findViewById(R.id.animalAdditionalInfoTextView2);

        Intent intent = getIntent();
        if (intent != null) {
            incomingAnimal = intent.getParcelableExtra(ANIMAL_KEY);
            Glide.with(this)
                    .load(incomingAnimal.getImageUrl())
                    .into(imageView);
            if(GetCurrentLanguage.getCurrentLanguage(AnimalDetail.this).equals("en")){
                nameTextView.setText(incomingAnimal.getNameEn());
                descriptionTextView.setText(incomingAnimal.getDescriptionEn());
                respondTextView.setText(incomingAnimal.getRespondEn());
                toolbar.setTitle(incomingAnimal.getNameEn());
            }
            else {
                nameTextView.setText(incomingAnimal.getNameVi());
                descriptionTextView.setText(incomingAnimal.getDescriptionVi());
                respondTextView.setText(incomingAnimal.getRespondVi());
                toolbar.setTitle(incomingAnimal.getNameVi());
            }
            dangerTextView.setText(incomingAnimal.getDanger());
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBarAnimal);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    startActivity(new Intent(AnimalDetail.this, MainActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
