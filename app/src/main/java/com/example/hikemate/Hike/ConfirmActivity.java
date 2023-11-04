package com.example.hikemate.Hike;

import static com.example.hikemate.Hike.HikeActivity.HIKE_IMAGE;
import static com.example.hikemate.Hike.HikeDetail.HIKE_KEY;
import static com.example.hikemate.MainActivity.SHOW_FRAGMENT;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.palette.graphics.Palette;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.MainActivity;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

public class ConfirmActivity extends AppCompatActivity {

    private ShapeableImageView imageHike;
    private TextView txtHikeName, txtHikeLength, txtHikeDate, txtLocation, txtDifficulty, txtDescription, txtParkingAvailable;
    private Button btnCancel, btnConfirm;
    private Hike incomingHike;
    private HikeImage incomingHikeImage;
    private HikeDatabase db;
    private MaterialToolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_confirm);
        getSupportActionBar().hide();

        initView();
        assignData();
        initListener();

    }

    private void assignData() {
        Intent intent = getIntent();
        if(intent != null) {
            incomingHike = intent.getParcelableExtra(HIKE_KEY);
            if (incomingHike != null) {
                txtHikeName.setText(incomingHike.getHikeName());
                txtHikeLength.setText(String.valueOf(incomingHike.getLength()));
                txtHikeDate.setText(TimeConverter.formattedDate(incomingHike.getDate()));
                txtLocation.setText(incomingHike.getLocation());
                txtDescription.setText(incomingHike.getDescription());
                if (incomingHike.isParkingAvailable()){
                    txtParkingAvailable.setText("Yes");
                } else {
                    txtParkingAvailable.setText("No");
                }
                if(incomingHike.getDifficulty() == 1){
                    txtDifficulty.setText("Easy");
                }else if (incomingHike.getDifficulty() == 2){
                    txtDifficulty.setText("Moderate");
                }else{
                    txtDifficulty.setText("Difficult");
                }
            }
            incomingHikeImage = intent.getParcelableExtra(HIKE_IMAGE);
            if (incomingHikeImage != null){
                Glide.with(ConfirmActivity.this)
                        .asBitmap()
                        .load(incomingHikeImage.getData())
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                // Handle failed image loading
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                // Image loaded successfully
                                Palette.from(resource).generate(palette -> {
                                    int defaultColor = ContextCompat.getColor(ConfirmActivity.this, R.color.white);
                                    int dominantColor = palette.getDominantColor(defaultColor);
                                    imageHike.setBackgroundColor(dominantColor);
                                });
                                return false;
                            }
                        })
                        .placeholder(R.drawable.baseline_restart_alt_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .into(imageHike);
            }
        }
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long id = db.hikeDao().insert(incomingHike);
                incomingHikeImage.setHikeId((int) id);
                db.hikeImageDao().insertImage(incomingHikeImage);
                Toast.makeText(ConfirmActivity.this, getApplicationContext().getString(R.string.successful_added_the_hike_with_id) + id, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ConfirmActivity.this, MainActivity.class);
                intent.putExtra(SHOW_FRAGMENT, "hikeList"); // Pass a unique identifier for the fragment
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        imageHike = findViewById(R.id.imageHike);
        txtHikeName = findViewById(R.id.txtHikeName);
        txtHikeLength = findViewById(R.id.txtHikeLength);
        txtHikeDate = findViewById(R.id.txtHikeDate);
        txtLocation = findViewById(R.id.txtLocation);
        txtDifficulty = findViewById(R.id.txtDifficulty);
        txtDescription = findViewById(R.id.txtDescription);
        txtParkingAvailable = findViewById(R.id.txtParkingAvailable);
        btnCancel = findViewById(R.id.btnCancel);
        toolbar = findViewById(R.id.toolbarConfirmHike);
        btnConfirm = findViewById(R.id.btnConfirm);
        db = HikeDatabase.getInstance(ConfirmActivity.this);
    }
}