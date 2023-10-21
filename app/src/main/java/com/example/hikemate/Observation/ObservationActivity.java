package com.example.hikemate.Observation;

import static com.example.hikemate.Hike.HikeDetail.HIKE_ID;
import static com.example.hikemate.MainActivity.SHOW_FRAGMENT;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageOptions;
import com.example.hikemate.Database.Dao.ObservationDao;
import com.example.hikemate.Database.Dao.ObservationImageDao;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.Database.Model.ObservationImage;
import com.example.hikemate.Hike.HikeDetail;
import com.example.hikemate.MainActivity;
import com.example.hikemate.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.canhub.cropper.CropImageContractOptions;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ObservationActivity extends AppCompatActivity {
    private HikeDatabase db;
    private TextInputLayout textInputLayoutObservation, textInputLayoutObservationTime, textInputLayoutAdditionalComments;
    private TextInputEditText edtObservation, edtObservationTime, edtComment;
    private ShapeableImageView imageObservation;
    private Button btnSelectImage, btnClear, btnViewObservationList, btnCreate;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private Button takeImage;
    private Bitmap bitmapImageHike;
    private Long date;
    private TextView txtWarning;
    private MaterialDatePicker<Long> datePicker;
    private MaterialToolbar toolbarCreateObservation;
    private CircularProgressIndicator progressCalculate;
    private Hike incomingHike;
    private ObservationDao observationDao;
    private ObservationImageDao observationImageDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_observation);
        getSupportActionBar().hide();

        initView();

        initListener();


        initializeDatePicker();

        initImagePickerNew();

        HikeDatabase db = HikeDatabase.getInstance(this);
        observationDao = db.observationDao();
        observationImageDao = db.observationImageDao();

        Intent intent = getIntent();
        incomingHike = intent.getParcelableExtra(HIKE_ID);




        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(new Date(todayInMillis));
        edtObservationTime.setText(todayDate);
        date = todayInMillis;

        if (checkCameraPermission()) {
            takeImage.setEnabled(true);
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    private void initImagePickerNew() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {// Check if the selected image size exceeds the threshold (e.g., 1 MB)
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
                    Toast.makeText(this, "The image is too large! Please select another.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                // Load the selected image into the ImageView
                txtWarning.setVisibility(View.GONE);
                bitmapImageHike = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                imageObservation.setVisibility(View.VISIBLE);
                imageObservation.setImageBitmap(bitmapImageHike);
            }
        });
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
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
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

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnViewObservationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ObservationActivity.this, ObservationList.class));
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtObservation.setText("");
                edtObservationTime.setText("");
                edtComment.setText("");
                bitmapImageHike = null;
                imageObservation.setVisibility(View.GONE);
            }
        });

        edtObservation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutObservation.setError(null);
                }
            }
        });

        edtObservationTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutObservationTime.setError(null);
                }
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    CropImageOptions cropImageOptions = new CropImageOptions();
                    cropImageOptions.imageSourceIncludeGallery = true;
                    cropImageOptions.imageSourceIncludeCamera = false;
                    cropImageOptions.activityMenuIconColor = R.color.black;
                    CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(getImageUriFromBitmap((Bitmap) bundle.get("data")), cropImageOptions);
                    cropImage.launch(cropImageContractOptions);
                }
            }
    );

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private void save() {
        String observationName = edtObservation.getText().toString();
        String comments = edtComment.getText().toString();
        if (observationName.isEmpty()) {
            textInputLayoutObservation.setError("Please enter the Observation name!");
            Toast.makeText(this, "Please enter the Observation name!", Toast.LENGTH_SHORT).show();
        } else if (date == null) {
            textInputLayoutObservationTime.setError("Please select the date of Observation!");
            Toast.makeText(this, "Please select the date of Observation!", Toast.LENGTH_LONG).show();
        } else if (bitmapImageHike == null) {
            txtWarning.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_LONG).show();
        } else {
            txtWarning.setVisibility(View.GONE);
            int hikeId = incomingHike.getId();
            long id = observationDao.insert(new Observation(hikeId, observationName, date, comments));
            observationImageDao.insertImage(new ObservationImage(bitmapImageHike, (int) id));
            Toast.makeText(this, "Successfully added the Observation with ID: " + id, Toast.LENGTH_SHORT).show();
            onBackPressed();
            edtObservation.setText("");
            edtObservationTime.setText("");
            edtComment.setText("");
            bitmapImageHike = null;
            imageObservation.setVisibility(View.GONE);
        }
    }

    private void initView() {
        textInputLayoutObservation = findViewById(R.id.textInputLayoutObservation);
        textInputLayoutObservationTime = findViewById(R.id.textInputLayoutObservationTime);
        textInputLayoutAdditionalComments= findViewById(R.id.textInputLayoutAdditionalComments);
        progressCalculate = findViewById(R.id.progressCalculate);
        edtObservation = findViewById(R.id.edtObservation);
        edtObservationTime = findViewById(R.id.edtObservationTime);
        edtComment = findViewById(R.id.edtComment);
        imageObservation = findViewById(R.id.imageObservation);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnClear = findViewById(R.id.btnClear);
        btnViewObservationList = findViewById(R.id.btnViewObservationList);
        btnCreate = findViewById(R.id.btnCreate);
        txtWarning = findViewById(R.id.txtWarning);
        toolbarCreateObservation = findViewById(R.id.toolbarCreateObservation);
        takeImage = findViewById(R.id.btnCaptureImage);
        db = HikeDatabase.getInstance(ObservationActivity.this);
    }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
