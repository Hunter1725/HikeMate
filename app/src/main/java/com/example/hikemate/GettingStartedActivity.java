package com.example.hikemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.hikemate.Database.HikeDatabase;
import com.google.android.material.tabs.TabLayout;

import java.util.Locale;

public class GettingStartedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getting_started);

        ViewPager viewPager = findViewById(R.id.viewPager);
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
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
            public int getCount() {
                return 4; // Number of fragments
            }
        };

        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.getting_started_custom_tab);
            }
        }

        Button nextButton = findViewById(R.id.nextButton);
        Button prevButton = findViewById(R.id.prevButton);
        Button okButton = findViewById(R.id.okButton);

        final int lastTabPosition = pagerAdapter.getCount() - 1;

// Set initial visibility for "OK" button and "Next/Previous" buttons
        if (viewPager.getCurrentItem() == lastTabPosition) {
            nextButton.setVisibility(View.GONE);
            prevButton.setVisibility(View.GONE);
            okButton.setVisibility(View.VISIBLE);
        } else {
            nextButton.setVisibility(View.VISIBLE);
            prevButton.setVisibility(View.VISIBLE);
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
                    // You can handle any specific action for this fragment here
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager.getCurrentItem();
                if (currentItem > 0) {
                    viewPager.setCurrentItem(currentItem - 1);
                }
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
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // Update button visibility based on the current fragment
                if (position == lastTabPosition) {
                    nextButton.setVisibility(View.GONE);
                    prevButton.setVisibility(View.GONE);
                    okButton.setVisibility(View.VISIBLE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                    okButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
