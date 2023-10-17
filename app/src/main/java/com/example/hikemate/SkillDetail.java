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
import com.example.hikemate.Database.Model.Plant;
import com.example.hikemate.Database.Model.Skill;
import com.google.android.material.appbar.MaterialToolbar;

public class SkillDetail extends AppCompatActivity {
    public static final String SKILL_KEY = "skill_key";
    private Skill incomingSkill;
    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_skill_detail);
        getSupportActionBar().hide();

        initView();
        initListener();

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
                toolbar.setTitle(incomingSkill.getNameEn());
            }
            else {
                nameTextView.setText(incomingSkill.getNameVi());
                descriptionTextView.setText(incomingSkill.getDescriptionVi());
                respondTextView.setText(incomingSkill.getRespondVi());
                toolbar.setTitle(incomingSkill.getNameVi());
            }

            dangerTextView.setText(incomingSkill.getDanger());

        }
    }

    private void initView() {
        toolbar = findViewById(R.id.toolBarSkill);
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
                    startActivity(new Intent(SkillDetail.this, MainActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}