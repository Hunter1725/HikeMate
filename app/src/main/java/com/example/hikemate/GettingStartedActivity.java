package com.example.hikemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hikemate.Database.HikeDatabase;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Locale;

public class GettingStartedActivity extends AppCompatActivity {
    private HikeDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);


        ViewPager2 viewPager = findViewById(R.id.viewPager);

        db = HikeDatabase.getInstance(this);

        FragmentStateAdapter pagerAdapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                // Return the appropriate fragment for the given position
                switch (position) {
                    case 0:
                        return new StartFragment1();
                    case 1:
                        return new StartFragment2();
                    case 2:
                        return new StartFragment3();
                    case 3:
                        return new StartFragment4();
                    default:
                        return null;
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Number of fragments
            }
        };

        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View customTabView = LayoutInflater.from(this).inflate(R.layout.getting_started_custom_tab, null);
            tab.setCustomView(customTabView);
        }).attach();

        Button nextButton = findViewById(R.id.nextButton);
        Button skipButton = findViewById(R.id.skipButton);
        Button okButton = findViewById(R.id.okButton);

        final int lastTabPosition = pagerAdapter.getItemCount() - 1;

        // Set initial visibility for "OK" button and "Next/Previous" buttons
        if (viewPager.getCurrentItem() == lastTabPosition) {
            nextButton.setVisibility(View.GONE);
            skipButton.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            skipButton.setVisibility(View.VISIBLE);
            okButton.setVisibility(View.GONE);
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem < lastTabPosition) {
                    viewPager.setCurrentItem(currentItem + 1);
                } else if (currentItem == lastTabPosition) {
                    // This is Fragment 4 (last fragment)
                }
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the new activity when "OK" button is clicked
                startActivity(new Intent(GettingStartedActivity.this, MainActivity.class));
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch the new activity when "OK" button is clicked
                startActivity(new Intent(GettingStartedActivity.this, MainActivity.class));
            }
        });

        // Add a ViewPager change listener to handle button visibility
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Update button visibility based on the current fragment
                if (position == lastTabPosition) {
                    nextButton.setVisibility(View.GONE);
                    skipButton.setVisibility(View.GONE);
                    okButton.setVisibility(View.VISIBLE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                    skipButton.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        HikeDatabase db = HikeDatabase.getInstance(this);
        String lng = db.settingDao().getSetting().getLanguage();
        Locale locale;
        locale = new Locale(lng);
        Locale.setDefault(locale);

        Context context = ContextWrapper.wrap(newBase, locale);
        super.attachBaseContext(context);
    }
}
