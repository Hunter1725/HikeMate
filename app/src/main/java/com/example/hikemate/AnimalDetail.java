package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Database.Model.Animal;

public class AnimalDetail extends AppCompatActivity {
    public static final String ANIMAL_KEY = "animal_key";
    private Animal incomingAnimal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_detail);

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
            }
            else {
                nameTextView.setText(incomingAnimal.getNameVi());
                descriptionTextView.setText(incomingAnimal.getDescriptionVi());
                respondTextView.setText(incomingAnimal.getRespondVi());
            }

            dangerTextView.setText(incomingAnimal.getDanger());

        }
    }
}
