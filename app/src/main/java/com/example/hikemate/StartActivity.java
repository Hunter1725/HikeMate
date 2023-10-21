package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.hikemate.ChatBot.ChatActivity;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Setting;
import com.example.hikemate.Hike.HikeActivity;
import com.example.hikemate.Setting.WebsiteActivity;
import com.google.android.material.card.MaterialCardView;

public class StartActivity extends AppCompatActivity {

    private MaterialCardView cardView, cardView2;
    private HikeDatabase db;
    private String langCode = "en";
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
        getSupportActionBar().hide();
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
                select.setText("Explore Nature's Wonders - Enhance Your Health!");
                btnAccept.setText("Accept");
                termAndPrivacyEn("I read and accept the terms of use and the privacy policy");
                langCode = "en";
            }
        });

        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setChecked(false);
                cardView2.setChecked(true);
                select.setText("Khám phá kì quan thiên nhiên - Cải thiện sức khoẻ");
                btnAccept.setText("Chấp nhận");
                termAndPrivacyVi("Tôi đã đọc và chấp nhận các điều khoản sử dụng và chính sách quyền riêng tư");
                langCode = "vi";
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.settingDao().insert(new Setting(langCode,true));
                startActivity(new Intent(StartActivity.this, GettingStartedActivity.class));
            }
        });

        termAndPrivacyEn("I read and accept the terms of use and the privacy policy");
    }

    private void termAndPrivacyVi(String mainText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mainText);

        // Apply different text styles to specific parts of the text
        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        StyleSpan boldStyle2 = new StyleSpan(Typeface.BOLD);

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(StartActivity.this, WebsiteActivity.class);
                String message = "https://www.freeprivacypolicy.com/live/0fa5afbc-8836-4aec-8c85-d6f10530d399";
                intent.putExtra("TERMS_KEY", message);
                startActivity(intent);
            }
        };

        ClickableSpan privacyPolicyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(StartActivity.this, WebsiteActivity.class);
                String message = "https://www.freeprivacypolicy.com/live/e5a4b424-1b2a-4fee-ab8a-5b5db5ab7d96";
                intent.putExtra("PRIVACY_KEY", message);
                startActivity(intent);
            }
        };

        // Set the color span for the "terms of use" part
        spannableStringBuilder.setSpan(boldStyle, mainText.indexOf("các điều khoản sử dụng"), mainText.indexOf("các điều khoản sử dụng") + "các điều khoản sử dụng".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the clickable span for the "terms of use" part
        spannableStringBuilder.setSpan(termsClickableSpan, mainText.indexOf("các điều khoản sử dụng"), mainText.indexOf("các điều khoản sử dụng") + "các điều khoản sử dụng".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the color span for the "privacy policy" part
        spannableStringBuilder.setSpan(boldStyle2, mainText.indexOf("chính sách quyền riêng tư"), mainText.indexOf("chính sách quyền riêng tư") + "chính sách quyền riêng tư".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the clickable span for the "privacy policy" part
        spannableStringBuilder.setSpan(privacyPolicyClickableSpan, mainText.indexOf("chính sách quyền riêng tư"), mainText.indexOf("chính sách quyền riêng tư") + "chính sách quyền riêng tư".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveReadPrivacyAndTerms.setText(spannableStringBuilder);
        haveReadPrivacyAndTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void termAndPrivacyEn(String mainText) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(mainText);

        // Apply different text styles to specific parts of the text
        StyleSpan boldStyle = new StyleSpan(Typeface.BOLD);
        StyleSpan boldStyle2 = new StyleSpan(Typeface.BOLD);

        ClickableSpan termsClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(StartActivity.this, WebsiteActivity.class);
                String message = "https://www.freeprivacypolicy.com/live/fba53041-2e38-458d-b06e-93185d3ad3f4";
                intent.putExtra("TERMS_KEY", message);
                startActivity(intent);
            }
        };

        ClickableSpan privacyPolicyClickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(StartActivity.this, WebsiteActivity.class);
                String message = "https://www.freeprivacypolicy.com/live/69dd2b30-6786-442a-922c-3d06b24ffd5e";
                intent.putExtra("PRIVACY_KEY", message);
                startActivity(intent);
            }
        };

        // Set the color span for the "terms of use" part
        spannableStringBuilder.setSpan(boldStyle, mainText.indexOf("terms of use"), mainText.indexOf("terms of use") + "terms of use".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the clickable span for the "terms of use" part
        spannableStringBuilder.setSpan(termsClickableSpan, mainText.indexOf("terms of use"), mainText.indexOf("terms of use") + "terms of use".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the color span for the "privacy policy" part
        spannableStringBuilder.setSpan(boldStyle2, mainText.indexOf("privacy policy"), mainText.indexOf("privacy policy") + "privacy policy".length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Set the clickable span for the "privacy policy" part
        spannableStringBuilder.setSpan(privacyPolicyClickableSpan, mainText.indexOf("privacy policy"), mainText.indexOf("privacy policy") + "privacy policy".length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        haveReadPrivacyAndTerms.setText(spannableStringBuilder);
        haveReadPrivacyAndTerms.setMovementMethod(LinkMovementMethod.getInstance());
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