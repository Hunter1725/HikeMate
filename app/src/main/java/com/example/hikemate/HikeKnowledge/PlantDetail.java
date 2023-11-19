package com.example.hikemate.HikeKnowledge;

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
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Other.GetCurrentLanguage;
import com.example.hikemate.MainActivity;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;

public class PlantDetail extends AppCompatActivity {
    public static final String PLANT_KEY = "plant_key";
    private Plant incomingPlan;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_plant_detail);
        getSupportActionBar().hide();

        initView();
        initListener();

        ImageView imageView = findViewById(R.id.imageView4);
        TextView nameTextView = findViewById(R.id.plantNameTextView2);
        TextView dangerTextView = findViewById(R.id.plantNameTextView4);
        TextView descriptionTextView = findViewById(R.id.plantAdditionalInfoTextView);
        TextView respondTextView = findViewById(R.id.plantAdditionalInfoTextView2);

        Intent intent = getIntent();
        if (intent != null) {
            incomingPlan = intent.getParcelableExtra(PLANT_KEY);
            Glide.with(this)
                    .load(incomingPlan.getImageUrl())
                    .into(imageView);
            if(GetCurrentLanguage.getCurrentLanguage(PlantDetail.this).equals("en")){
                nameTextView.setText(incomingPlan.getNameEn());
                descriptionTextView.setText(incomingPlan.getDescriptionEn());
                respondTextView.setText(incomingPlan.getRespondEn());
                toolbar.setTitle(incomingPlan.getNameEn());
            }
            else {
                nameTextView.setText(incomingPlan.getNameVi());
                descriptionTextView.setText(incomingPlan.getDescriptionVi());
                respondTextView.setText(incomingPlan.getRespondVi());
                toolbar.setTitle(incomingPlan.getNameEn());
            }
            dangerTextView.setText(incomingPlan.getDanger());
        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBarPlant);
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
                    startActivity(new Intent(PlantDetail.this, MainActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}