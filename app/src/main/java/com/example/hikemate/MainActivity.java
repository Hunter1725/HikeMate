package com.example.hikemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hikemate.ChatBot.ChatActivity;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Hike.HikeList;
import com.example.hikemate.Hike.SearchHike;
import com.example.hikemate.HikeKnowledge.LibraryActivity;
import com.example.hikemate.Other.ContextWrapper;
import com.example.hikemate.Setting.SettingActivity;
import com.example.hikemate.Setting.WebsiteActivity;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.example.hikemate.Hike.HikeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final String SHOW_FRAGMENT = "showFragment";
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private HikeDatabase db;
    private NestedScrollView nestedScrollView;
    private FloatingActionButton fabScrollToTop;
    public static final int CAMERA_PERMISSION_CODE = 101;
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
        requestCameraUpdatesWithPermission();
    }

    private void requestCameraUpdatesWithPermission() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "Granted camera permission!", Toast.LENGTH_SHORT).show();
        } else {
            // Request permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // User has previously denied the permission, show a rationale and request again if needed
                showSnackbar();
            } else {
                // Request the permissions directly
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.CAMERA
                }, CAMERA_PERMISSION_CODE);
            }
        }
    }

    private void showSnackbar() {
        Snackbar.make(findViewById(android.R.id.content), "Camera permission is required for this app to work.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permissions when the "Grant" button is clicked in the Snackbar
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.CAMERA
                        }, CAMERA_PERMISSION_CODE);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            // Check if the permissions were granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted, proceed with requesting location updates
                Toast.makeText(this, "Camera permissions are granted for this app to work.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permissions are not granted for this app to work.", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void initListener() {

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.search) {
                    startActivity(new Intent(MainActivity.this, SearchHike.class));
                    return true;
                }
                return false;
            }
        });

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
                    replaceFragment(new HikeList());
                    bottomNavigationView.setSelectedItemId(R.id.hikeBottom);

                } else if (itemId == R.id.newPlan) {
                    startActivity(new Intent(MainActivity.this, HikeActivity.class));
                } else if (itemId == R.id.chatSupporter) {
                    startActivity(new Intent(MainActivity.this, ChatActivity.class));
                } else if (itemId == R.id.weather) {
                    startActivity(new Intent(MainActivity.this, WeatherActivity.class));
                } else if (itemId == R.id.setting) {
                    startActivity(new Intent(MainActivity.this, SettingActivity.class));
                } else if (itemId == R.id.term) {
                    Intent intent = new Intent(MainActivity.this, WebsiteActivity.class);
                    String message = "https://www.freeprivacypolicy.com/live/fba53041-2e38-458d-b06e-93185d3ad3f4";
                    intent.putExtra("TERMS_KEY", message);
                    startActivity(intent);
                } else if (itemId == R.id.licence) {
                    Intent intent = new Intent(MainActivity.this, WebsiteActivity.class);
                    String message = "https://www.freeprivacypolicy.com/live/69dd2b30-6786-442a-922c-3d06b24ffd5e";
                    intent.putExtra("PRIVACY_KEY", message);
                    startActivity(intent);
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

        fabScrollToTop.hide();
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // Check the scroll position
                if (scrollY == 0) {
                    // Scroll is at the top, hide the FloatingActionButton
                    fabScrollToTop.hide();
                } else if (scrollY > oldScrollY) {
                    // Scrolling downwards, hide the FloatingActionButton
                    fabScrollToTop.hide();
                } else {
                    // Scrolling upwards, show the FloatingActionButton
                    fabScrollToTop.show();
                }
            }
        });

        //FAB scroll up
        // Set an OnClickListener for the FloatingActionButton
        fabScrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Scroll to the top of the NestedScrollView
                nestedScrollView.smoothScrollTo(0, 0);
            }
        });
    }

    private void initView() {
        drawer = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        fabScrollToTop = findViewById(R.id.fabScrollToTop);
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
                    .setTitle(R.string.exit)
                    .setMessage(R.string.are_you_sure_you_want_to_exit_the_app)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Exit the app
                            finishAffinity();
                        }
                    })
                    .setNegativeButton(R.string.no, null)
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