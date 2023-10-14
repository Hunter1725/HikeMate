package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Database.Model.Animal;
import com.example.hikemate.Database.Model.Plant;

public class PlantDetail extends AppCompatActivity {
    public static final String PLANT_KEY = "plant_key";
    private Plant incomingPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_detail);

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
            }
            else {
                nameTextView.setText(incomingPlan.getNameVi());
                descriptionTextView.setText(incomingPlan.getDescriptionVi());
                respondTextView.setText(incomingPlan.getRespondVi());
            }

            dangerTextView.setText(incomingPlan.getDanger());

        }
    }
}