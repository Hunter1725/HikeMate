package com.example.hikemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.view.View;
import android.widget.Button;

import com.example.hikemate.ChatBot.ChatActivity;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Hike.HikeList;
import com.example.hikemate.Maps.MapsActivity;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.example.hikemate.Hike.HikeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String SHOW_FRAGMENT = "showFragment";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private HikeDatabase db;
    private NestedScrollView nestedScrollView;

    private Button testButton, anotherTestButton, skillTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        initView();

        Intent intent = getIntent();
        if (intent != null) {
            String fragmentToShow = intent.getStringExtra(SHOW_FRAGMENT);
            if ("mainFragment".equals(fragmentToShow)) {
                // Show the desired fragment using FragmentTransaction
                replaceFragment(new MainFragment());
                bottomNavigationView.setSelectedItemId(R.id.homeBottom);
            } else if ("hikeList".equals(fragmentToShow)) {
                replaceFragment(new HikeList());
                bottomNavigationView.setSelectedItemId(R.id.hikeBottom);
            }
        }

//        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        initListener();
    }

    private void initListener() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                // Set checked state for the selected item
                item.setChecked(true);

                // Close the drawer
                drawer.closeDrawers();

                if (itemId == R.id.allPlan) {
                    // Handle "All plans" item selection
                    startActivity(new Intent(MainActivity.this, WeatherActivity.class));
                } else if (itemId == R.id.newPlan) {
                    startActivity(new Intent(MainActivity.this, MapsActivity.class));
                }
                return false;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle item selection
                int itemId = item.getItemId();

                // Set checked state for the selected item
                item.setChecked(true);

                if (itemId == R.id.homeBottom) {
                    replaceFragment(new MainFragment());
                } else if(itemId == R.id.hikeBottom) {
                    replaceFragment(new HikeList());
                }else if (itemId == R.id.chat) {
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                }else if (itemId == R.id.knowledge) {
                    replaceFragment(new LibraryActivity());
                }
                return true;
            }
        });
    }

    private void initView() {
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        db = HikeDatabase.getInstance(MainActivity.this);
        replaceFragment(new MainFragment());
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

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
            nestedScrollView.smoothScrollTo(0, 0);
        } else {
            // No remaining fragments, show exit confirmation dialog
            new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog2)
                    .setTitle("Exit")
                    .setMessage("Are you sure you want to exit the app?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Exit the app
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_app_bar, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem avatarItem = toolbar.getMenu().findItem(R.id.miniAvatar);
        avatarItem.setActionView(R.layout.menu_item_avatar);
        return super.onPrepareOptionsMenu(menu);
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        nestedScrollView.smoothScrollTo(0, 0);
    }

}