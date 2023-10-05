package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Setting;
import com.google.android.material.card.MaterialCardView;

public class StartActivity extends AppCompatActivity {

    private MaterialCardView cardView, cardView2;
    private HikeDatabase db;
    private String langCode;
    private Setting setting;
    private TextView select,haveReadPrivacyAndTerms;
    private Button btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_start);
        initView();
        if (db.settingDao().getSetting() != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
        initListener();
    }

    private void initListener() {
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setChecked(true);
                cardView2.setChecked(false);
                select.setText("Where Every Step Counts - Your Trail to Adventure!");
                btnAccept.setText("Accept");
                haveReadPrivacyAndTerms.setText("I read and accept the terms of use and the privacy policy");
                langCode = "en";
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setChecked(false);
                cardView2.setChecked(true);
                select.setText("Mỗi bước đi đều quan trọng - Hành trình của bạn đến với cuộc phiêu lưu!");
                btnAccept.setText("Chấp nhận");
                haveReadPrivacyAndTerms.setText("Tôi đã đọc và chấp nhận các điều khoản sử dụng và chính sách quyền riêng tư");
                langCode = "vi";
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.settingDao().insert(new Setting(langCode,true));
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            }
        });
    }

    private void initView() {
        cardView = findViewById(R.id.cardView);
        cardView2 = findViewById(R.id.cardView2);
        select = findViewById(R.id.select);
        haveReadPrivacyAndTerms = findViewById(R.id.haveReadPrivacyAndTerms);
        btnAccept = findViewById(R.id.btnAccept);
        db = HikeDatabase.getInstance(StartActivity.this);
    }
}