package com.example.hikemate.Hike;

import static com.example.hikemate.MainActivity.SHOW_FRAGMENT;
import static com.example.hikemate.Maps.MapsActivity.LATLNG_KEY;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.MainActivity;
import com.example.hikemate.Maps.MapsActivity;
import com.example.hikemate.Database.Model.Observation;
import com.example.hikemate.MainActivity;
import com.example.hikemate.Observation.ObservationActivity;
import com.example.hikemate.Observation.ObservationList;
import com.example.hikemate.R;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HikeDetail extends AppCompatActivity {
    public static final String HIKE_KEY = "hike_key";
    public static final String HIKE_ID = "HikeId";
    private HikeDatabase db;
    private ConstraintLayout imageLayout;
    private TextInputLayout textInputLayoutHikeName, textInputLayoutHikeLength, textInputLayoutHikeDate, textInputLayoutLocation, textInputLayoutDescription;
    private TextInputEditText edtHikeName, edtHikeLength, edtDoH, edtLocation, edtDescription;
    private RadioGroup radioDifficulty, radioParkingAvailable;
    private RadioButton btnEasy, btnModerate, btnDifficult, btnParkingAvailable, btnParkingUnavailable;
    private ShapeableImageView imageHike, iconCamera;
    private Button btnSelectImage, btnCancel, btnSave;
    private TextView txtHikeName, btnOpenMap;
    private Button  btnCreateObservation, btnViewObservation;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;
    private Bitmap bitmapImageHike;
    private Long date;
    private TextView txtWarning;
    private MaterialDatePicker<Long> datePicker;
    private Hike incomingHike;
    private HikeImage hikeImage;
    private int difficulty;
    private boolean parkingAvailable;
    private MaterialToolbar toolbarHike;
    private LatLng incomingLatlng;
    private RecyclerView recyclerViewObservation;
    private AlertDialog dialog;
    private static final int CAMERA_PERMISSION_CODE = 101;
    private ArrayList<Observation> observationList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.update_hike);
        getSupportActionBar().hide();

        initView();
        assignData();
        initializeDatePicker();
        initListener();
        initImagePickerNew();

    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }


    private void setupObservationListUI() {}
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
                bitmapImageHike = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                imageHike.setImageBitmap(bitmapImageHike);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!db.observationDao().getObservationsForHike(incomingHike.getId()).isEmpty()) {
            assignData();
        }
    }


    private void initializeDatePicker() {
        // Get today's date in milliseconds
        long todayInMillis = MaterialDatePicker.todayInUtcMilliseconds();

        // Create a CalendarConstraints.Builder
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Set a DateValidator using DateValidatorPointBackward.now()
        constraintsBuilder.setEnd(todayInMillis);

        // Build the CalendarConstraints
        CalendarConstraints constraints = constraintsBuilder.build();

        // Initialize the date picker
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(incomingHike.getDate())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build();


        edtDoH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    edtDoH.setHint("mm/dd/yyyy");
                    datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                    textInputLayoutHikeDate.setError(null);
                } else {
                    edtDoH.setHint(null);
                }
            }
        });

        edtDoH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtDoH.setHint("mm/dd/yyyy");
                datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
                textInputLayoutHikeDate.setError(null);
            }
        });

        // Set the date selection listener
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert the selected date to a formatted string
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            String selectedDate = dateFormat.format(new Date(selection));
            date = selection;

            // Set the selected date in the TextInputEditText
            edtDoH.setText(selectedDate);
        });
    }

    private void initListener() {
        toolbarHike.setNavigationOnClickListener(new View.OnClickListener() {
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
        btnViewObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HikeDetail.this, ObservationList.class);
                intent.putExtra(HIKE_ID, incomingHike);
                startActivity(intent);
            }
        });

        btnCreateObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HikeDetail.this, ObservationActivity.class);
                intent.putExtra(HIKE_ID, incomingHike);
                startActivity(intent);
            }
        });


        edtHikeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutHikeName.setError(null);
                }
            }
        });

        edtLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutLocation.setError(null);
                }
            }
        });

        edtHikeLength.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutHikeLength.setError(null);
                }
            }
        });

        edtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    textInputLayoutDescription.setError(null);
                }
            }
        });

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                pickMedia.launch(new PickVisualMediaRequest.Builder()
//                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
//                        .build());
                showDialogImage();
            }
        });
        radioDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btnEasy){
                    difficulty = 1;
                } else if (checkedId == R.id.btnModerate) {
                    difficulty = 2;
                } else if (checkedId == R.id.btnDifficult) {
                    difficulty = 3;
                }
            }
        });

        radioParkingAvailable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.btnParkingAvailable){
                    parkingAvailable = true;
                } else if (checkedId == R.id.btnParkingUnavailable) {
                    parkingAvailable = false;
                }
            }
        });

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HikeDetail.this, MapsActivity.class);
                intent.putExtra("activity","third");
                intent.putExtra(HIKE_KEY, incomingHike);
                startActivity(intent);
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

    private void showDialogImage() {
        // Inflate the dialog layout
        View dialogView = getLayoutInflater().inflate(R.layout.image_option, null);

        // Access the TextView in the dialog layout
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnCaptureImage = dialogView.findViewById(R.id.btnCaptureImage);

        if (checkCameraPermission()) {
            btnCaptureImage.setEnabled(true);
        } else {
            requestCameraPermission();
        }
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);
            }
        });

        // Create a MaterialAlertDialogBuilder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(HikeDetail.this, R.style.ThemeOverlay_App_MaterialAlertDialog2);
        builder.setView(dialogView)
                .setTitle("Update Image")
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }

    private void save(){
        if (edtHikeName.getText().toString().isEmpty()) {
            textInputLayoutHikeName.setError("Please enter the Hike name!");
            Toast.makeText(this, "Please enter the Hike name!", Toast.LENGTH_SHORT).show();
        }else if (edtHikeLength.getText().toString().isEmpty()) {
            textInputLayoutHikeLength.setError("Please enter the length of Hike!");
            Toast.makeText(this, "Please enter the length of Hike!", Toast.LENGTH_SHORT).show();
        }else if (edtDoH.getText().toString().isEmpty()) {
            textInputLayoutHikeDate.setError("Please select the date of Hike!");
            Toast.makeText(this, "Please select the date of Hike!", Toast.LENGTH_SHORT).show();
        }else if (edtLocation.getText().toString().isEmpty()) {
            textInputLayoutLocation.setError("Please select the location!");
            Toast.makeText(this, "Please select the location!", Toast.LENGTH_SHORT).show();
        }else if (edtDescription.getText().toString().isEmpty()) {
            textInputLayoutDescription.setError("Please select the description!");
            Toast.makeText(this, "Please select the description!", Toast.LENGTH_SHORT).show();
        }else{
            String hikeName = edtHikeName.getText().toString();
            String location = edtLocation.getText().toString();
            double length = Double.parseDouble(edtHikeLength.getText().toString());
            String description= edtDescription.getText().toString();

            incomingHike.setLocation(location);
            incomingHike.setHikeName(hikeName);
            incomingHike.setLength(length);
            incomingHike.setDate(date);
            incomingHike.setDescription(description);
            incomingHike.setDifficulty(difficulty);
            incomingHike.setParkingAvailable(parkingAvailable);

            db.hikeDao().update(incomingHike);
            hikeImage.setData(bitmapImageHike);
            db.hikeImageDao().updateImage(hikeImage);
            Toast.makeText(this, "Successful updated! ", Toast.LENGTH_SHORT).show();
            assignData();

        }

    }

    private void assignData() {
        Intent intent = getIntent();
        if(intent != null) {
            incomingHike = intent.getParcelableExtra(HIKE_KEY);
            if (incomingHike != null) {
                hikeImage = db.hikeImageDao().getHikeImageById(incomingHike.getId());
                toolbarHike.setTitle(incomingHike.getHikeName());
                txtHikeName.setText(incomingHike.getHikeName());
                edtHikeName.setText(incomingHike.getHikeName());
                String length = String.valueOf(incomingHike.getLength());
                edtHikeLength.setText(length);
                edtDoH.setText(TimeConverter.formattedDate(incomingHike.getDate()));
                edtLocation.setText(incomingHike.getLocation());
                edtDescription.setText(incomingHike.getDescription());
                parkingAvailable = incomingHike.isParkingAvailable();
                difficulty = incomingHike.getDifficulty();
                if(difficulty == 1){
                    btnEasy.setChecked(true);
                }else if (difficulty == 2){
                    btnModerate.setChecked(true);
                }else{
                    btnDifficult.setChecked(true);
                }
                if(parkingAvailable == true){
                    btnParkingAvailable.setChecked(true);
                }else {
                    btnParkingUnavailable.setChecked(true);
                }

                Glide.with(HikeDetail.this)
                        .asBitmap()
                        .load(hikeImage.getData())
                        .placeholder(R.drawable.baseline_restart_alt_24)
                        .error(R.drawable.baseline_error_outline_24)
                        .into(imageHike);
                bitmapImageHike = hikeImage.getData();
                date = incomingHike.getDate();
                //init RecycleViewObservation
                observationList = (ArrayList<Observation>) db.observationDao().getObservationsForHike(incomingHike.getId());
                ObservationItem observationItem = new ObservationItem(observationList, HikeDetail.this);
                recyclerViewObservation.setAdapter(observationItem);
                recyclerViewObservation.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false));
            }

            //Get Longitude and latitude
            incomingLatlng = intent.getParcelableExtra(LATLNG_KEY);
            if (incomingLatlng != null) {
                edtLocation.setText(getNameLocation(incomingLatlng));
            }
        }
    }

    private String getNameLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String addressText = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                addressText = address.getAddressLine(0); // Get the full address

                // Print or display the location name and address
                Log.d("LocationInfo", "Address: " + addressText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressText;
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
        textInputLayoutHikeName = findViewById(R.id.textInputLayoutHikeName);
        textInputLayoutHikeLength = findViewById(R.id.textInputLayoutHikeLength);
        textInputLayoutHikeDate = findViewById(R.id.textInputLayoutHikeDate);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        edtHikeName = findViewById(R.id.edtHikeName);
        edtHikeLength = findViewById(R.id.edtHikeLength);
        edtDoH = findViewById(R.id.edtDoH);
        edtLocation = findViewById(R.id.edtLocation);
        edtDescription = findViewById(R.id.edtDescription);
        radioDifficulty = findViewById(R.id.radioDifficulty);
        radioParkingAvailable = findViewById(R.id.radioParkingAvailable);
        btnEasy = findViewById(R.id.btnEasy);
        btnModerate = findViewById(R.id.btnModerate);
        btnDifficult = findViewById(R.id.btnDifficult);
        btnParkingAvailable = findViewById(R.id.btnParkingAvailable);
        btnParkingUnavailable = findViewById(R.id.btnParkingUnavailable);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        btnViewObservation = findViewById(R.id.btnViewObservation);
        btnCreateObservation = findViewById(R.id.btnCreateObservation);
        recyclerViewObservation = findViewById(R.id.recyclerViewObservation);

        txtWarning = findViewById(R.id.txtWarning);
        toolbarHike = findViewById(R.id.toolbarHike);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        txtHikeName = findViewById(R.id.txtHikeName);
        imageLayout = findViewById(R.id.imageLayout);
        imageHike = findViewById(R.id.imageHike);


        db = HikeDatabase.getInstance(HikeDetail.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(HikeDetail.this, MainActivity.class);
        intent.putExtra(SHOW_FRAGMENT, "hikeList"); // Pass a unique identifier for the fragment
        startActivity(intent);
    }
}