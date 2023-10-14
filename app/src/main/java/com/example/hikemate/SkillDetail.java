package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Model.Skill;

public class SkillDetail extends AppCompatActivity {
    public static final String SKILL_KEY = "skill_key";
    private Skill incomingSkill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_detail);

        ImageView imageView = findViewById(R.id.imageView4);
        TextView nameTextView = findViewById(R.id.skillNameTextView2);
        TextView dangerTextView = findViewById(R.id.skillNameTextView4);
        TextView descriptionTextView = findViewById(R.id.skillAdditionalInfoTextView);
        TextView respondTextView = findViewById(R.id.skillAdditionalInfoTextView2);

        Intent intent = getIntent();
        if (intent != null) {
            incomingSkill = intent.getParcelableExtra(SKILL_KEY);


            Glide.with(this)
                    .load(incomingSkill.getImageUrl())
                    .into(imageView);
            if(GetCurrentLanguage.getCurrentLanguage(SkillDetail.this).equals("en")){
                nameTextView.setText(incomingSkill.getNameEn());
                descriptionTextView.setText(incomingSkill.getDescriptionEn());
                respondTextView.setText(incomingSkill.getRespondEn());
            }
            else {
                nameTextView.setText(incomingSkill.getNameVi());
                descriptionTextView.setText(incomingSkill.getDescriptionVi());
                respondTextView.setText(incomingSkill.getRespondVi());
            }

            dangerTextView.setText(incomingSkill.getDanger());

        }
    }
}