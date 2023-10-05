package com.example.hikemate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.hikemate.Database.HikeDatabase;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
//            nestedScrollView.smoothScrollTo(0, 0);
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
}