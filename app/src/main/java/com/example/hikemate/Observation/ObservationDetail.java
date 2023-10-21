package com.example.hikemate.Observation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Database.Model.ObservationImage;
import com.example.hikemate.Hike.HikeDetail;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ObservationDetail extends AppCompatActivity {
    public static final String OBSERVATION_KEY = "observation_key";
    private HikeDatabase db;
    private ShapeableImageView imageObservation;
    private ConstraintLayout imageLayout;
    private TextInputLayout textInputLayoutObservationName, textInputLayoutObservationTime, textInputLayoutAdditionalComments;
    private TextView observationName;
    private TextInputEditText edtObservationName, edtObservationTime, edtComment;
    private Button btnCancel, btnSave;
    private MaterialToolbar toolbarObservationDetail;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    private Bitmap bitmapImageObservation;
    private Long date;
    private TextView txtWarning;
    private MaterialDatePicker<Long> datePicker;
    private Observation incomingObservation;
    private ObservationImage observationImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_observation_detail);
        getSupportActionBar().hide();

        initView();
        assignData();
        initializeDatePicker();
        initListener();
        initImagePickerNew();
    }

    private void assignData() {
        Intent intent = getIntent();
        if(intent != null) {
            incomingObservation = intent.getParcelableExtra(OBSERVATION_KEY);
            if(incomingObservation!= null){
                observationImage = db.observationImageDao().getObservationImageById(incomingObservation.getId());
                toolbarObservationDetail.setTitle(incomingObservation.getName());
                observationName.setText(incomingObservation.getName());
                edtObservationName.setText(incomingObservation.getName());
                edtObservationTime.setText(TimeConverter.formattedDate(incomingObservation.getTimeObservation()));
                edtComment.setText(incomingObservation.getAdditionalComment());

                Glide.with(ObservationDetail.this)
                        .asBitmap()
                        .load(observationImage.getData())
                        .placeholder(R.drawable.baseline_restart_alt_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .into(imageObservation);
                bitmapImageObservation = observationImage.getData();
                date = incomingObservation.getTimeObservation();
            }


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initializeDatePicker() {
        // Get today's date in milliseconds
        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();

        // Set a custom maximum date (e.g., 7 days from today)
        long customMaxDateInMillis = todayInMillis - (7 * 24 * 60 * 60 * 1000);

        // Create a CalendarConstraints.Builder
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Set a DateValidator using DateValidatorPointBackward.now()
        constraintsBuilder.setValidator(DateValidatorPointBackward.before(customMaxDateInMillis));
        constraintsBuilder.setEnd(todayInMillis);

        // Build the CalendarConstraints
        CalendarConstraints constraints = constraintsBuilder.build();

        // Initialize the date picker
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(incomingObservation.getTimeObservation())
                .setCalendarConstraints(constraints)
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build();


        edtObservationTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtObservationTime.setHint("mm/dd/yyyy");
                    datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                    textInputLayoutObservationTime.setError(null);
                } else {
                    edtObservationTime.setHint(null);
                }
            }
        });

        edtObservationTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtObservationTime.setHint("mm/dd/yyyy");
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                textInputLayoutObservationTime.setError(null);
            }
        });

        // Set the date selection listener
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selected date to a formatted string
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String selectedDate = dateFormat.format(new Date(selection));
            date = selection;

            // Set the selected date in the TextInputEditText
            edtObservationTime.setText(selectedDate);
        });
    }


    private void initListener() {

        toolbarObservationDetail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edtObservationName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutObservationName.setError(null);
                }
            }
        });

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        edtComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutAdditionalComments.setError(null);
                }
            }
        });

    }

    private void save(){
        if(edtObservationName.getText().toString().isEmpty()){
            textInputLayoutObservationName.setError("Please enter the Observation name!");
            Toast.makeText(this, "Please enter the Observation name!", Toast.LENGTH_SHORT).show();
        }else if(edtObservationTime.getText().toString().isEmpty()){
            textInputLayoutObservationTime.setError("Please select the Observation time!");
            Toast.makeText(this, "Please select the Observation time!", Toast.LENGTH_SHORT).show();
        }

        String name = edtObservationName.getText().toString();
        String comment = edtComment.getText().toString();
        incomingObservation.setName(name);
        incomingObservation.setTimeObservation(date);
        incomingObservation.setAdditionalComment(comment);
        db.observationDao().update(incomingObservation);
        observationImage.setData(bitmapImageObservation);
        db.observationImageDao().updateImage(observationImage);
        Toast.makeText(this, "Successful updated! ", Toast.LENGTH_SHORT).show();
        assignData();
        onBackPressed();
    }

    private void initImagePickerNew() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                // Check if the selected image size exceeds the threshold (e.g., 1 MB)
                long imageSize = getImageSize(uri);
                long threshold = 1024 * 1024; // 1 MB threshold

                if (imageSize <= threshold) {
                    // Image size is within the threshold, proceed with cropping
                    CropImageOptions cropImageOptions = new CropImageOptions();
                    cropImageOptions.imageSourceIncludeGallery = true;
                    cropImageOptions.imageSourceIncludeCamera = false;
                    cropImageOptions.activityMenuIconColor = R.color.black;
                    CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
                    cropImage.launch(cropImageContractOptions);

                    Log.d("PhotoPicker", "Selected URI: " + uri);
                } else {
                    // Image size exceeds the threshold, handle accordingly (e.g., show an error message)
                    Log.d("PhotoPicker", "Selected image size exceeds the threshold.");
                    Toast.makeText(this, "The image is to large! Please select another!", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                // Load the selected image into the ImageView
                bitmapImageObservation = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                imageObservation.setImageBitmap(bitmapImageObservation);
            }
        });
    }

    // Helper method to get the size of an image file given its URI
    private long getImageSize(Uri imageUri) {
        Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (sizeIndex != -1) {
                long size = cursor.getLong(sizeIndex);
                cursor.close();
                return size;
            }
            cursor.close();
        }
        return 0;
    }

    private void initView() {
        observationName = findViewById(R.id.observationName);
        edtObservationTime = findViewById(R.id.edtObservationTime);
        edtComment = findViewById(R.id.edtComment);
        edtObservationName = findViewById(R.id.edtObservationName);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);

        imageObservation = findViewById(R.id.imageObservation);
        imageLayout = findViewById(R.id.imageLayout);
        toolbarObservationDetail = findViewById(R.id.toolbarObservationDetail);
        textInputLayoutObservationTime = findViewById(R.id.textInputLayoutObservationTime);
        textInputLayoutAdditionalComments = findViewById(R.id.textInputLayoutAdditionalComments);
        textInputLayoutObservationName = findViewById(R.id.textInputLayoutObservationName);

        db = HikeDatabase.getInstance(ObservationDetail.this);

    }
}