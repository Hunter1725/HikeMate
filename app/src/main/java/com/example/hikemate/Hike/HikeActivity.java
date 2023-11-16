package com.example.hikemate.Hike;

import static com.example.hikemate.Hike.HikeDetail.HIKE_KEY;
import static com.example.hikemate.MainActivity.CAMERA_PERMISSION_CODE;
import static com.example.hikemate.MainActivity.SHOW_FRAGMENT;
import static com.example.hikemate.Maps.MapsActivity.LATLNG_KEY;
import static com.example.hikemate.WeatherForecast.WeatherActivity.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.hikemate.WeatherForecast.WeatherActivity.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageOptions;
import com.example.hikemate.Converter.TimeConverter;
import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.MainActivity;
import com.example.hikemate.Maps.MapsActivity;
import com.example.hikemate.R;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.canhub.cropper.CropImageContractOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HikeActivity extends AppCompatActivity {
    public static final String HIKE_IMAGE = "hike_image";
    private HikeDatabase db;
    private TextInputLayout textInputLayoutHikeName, textInputLayoutHikeLength, textInputLayoutHikeDate, textInputLayoutLocation, textInputLayoutDescription;
    private TextInputEditText edtHikeName, edtHikeLength, edtDoH, edtLocation, edtDescription;
    private RadioGroup radioDifficulty, radioParkingAvailable;
    private RadioButton btnEasy, btnModerate, btnDifficult, btnParkingAvailable, btnParkingUnavailable;
    private ShapeableImageView imgHike;
    private Button btnSelectImage, btnClear, btnCreate;
    private FusedLocationProviderClient fusedLocationClient;

    private Bitmap bitmapImageHike;
    private Long date;
    private TextView txtWarning, btnOpenMap;
    private MaterialDatePicker<Long> datePicker;
    private int difficulty = 1;
    private boolean parkingAvailable = true;
    private String description = "Empty";
    private MaterialToolbar toolbarCreateHike;
    private CircularProgressIndicator progressCalculate;
    private LatLng incomingLatlng;
    private Hike incomingHike;
    private HikeImage hikeImage;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<CropImageContractOptions> cropImage;

    private Button takeImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.create_hike);
        getSupportActionBar().hide();

        initView();

        assignData();

        initListener();
//        if (checkCameraPermission()) {
//            takeImage.setEnabled(true);
//        }
//        else {
//            requestCameraPermission();
//        }

        initializeDatePicker();

        initImagePickerNew();

        Intent intent = getIntent();
        if (intent != null) {
            incomingLatlng = intent.getParcelableExtra(LATLNG_KEY);
            if (incomingLatlng != null) {
                progressCalculate.setVisibility(View.GONE);
                getNameLocation(incomingLatlng);
            } else {
                requestLocationUpdates();
                fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            }
        }
    }

    private void assignData() {
        Intent intent = getIntent();
        if(intent != null) {
            incomingHike = intent.getParcelableExtra(HIKE_KEY);
            if (incomingHike != null) {
                hikeImage = intent.getParcelableExtra(HIKE_IMAGE);
                edtHikeName.setText(incomingHike.getHikeName());
                String length = String.valueOf(incomingHike.getLength());
                edtHikeLength.setText(length);
                if(incomingHike.getDate() != null)
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

                if(hikeImage.getData() != null) {
                    imgHike.setVisibility(View.VISIBLE);
                    Glide.with(HikeActivity.this)
                            .asBitmap()
                            .load(hikeImage.getData())
                            .placeholder(R.drawable.baseline_restart_alt_24)
                            .error(R.drawable.baseline_error_outline_24)
                            .into(imgHike);
                }
                bitmapImageHike = hikeImage.getData();
                date = incomingHike.getDate();
            }
        }
    }


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            if (location != null) {
                progressCalculate.setVisibility(View.GONE);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                getNameLocation(new LatLng(latitude, longitude));
            }
        }
    };

    private void getNameLocation(LatLng latLng) {
        edtLocation.setText(reverseGeocodeAddress(latLng));
    }

    private String reverseGeocodeAddress(LatLng latLng) {
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

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check if the necessary location settings are satisfied
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // All location settings are satisfied. Request location updates here.
                        requestLocationUpdatesWithPermission();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ResolvableApiException) {
                            // Location settings are not satisfied, but the user can resolve it.
                            try {
                                // Show a dialog to ask the user to enable location settings.
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(HikeActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        } else {
                            // Location settings are not satisfied, and cannot be resolved directly.
                            // Handle this case appropriately (e.g., show an error message).
                            Toast.makeText(HikeActivity.this, "Location settings are not satisfied.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User enabled location settings. Request location updates with permission.
                requestLocationUpdatesWithPermission();
            } else {
                // User canceled or didn't enable location settings. Handle this case appropriately.
                progressCalculate.setVisibility(View.GONE);
                Toast.makeText(HikeActivity.this, getApplicationContext().getString(R.string.location_settings_are_not_enabled), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void requestLocationUpdatesWithPermission() {
        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permissions are already granted, proceed with requesting location updates
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
            progressCalculate.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Getting your location!", Toast.LENGTH_LONG).show();
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        } else {
            // Request permissions
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // User has previously denied the permission, show a rationale and request again if needed
                showSnackbar();
            } else {
                // Request the permissions directly
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void showSnackbar() {
        // Show a Snackbar to explain the need for permissions and prompt the user to grant them
        Snackbar.make(findViewById(android.R.id.content), HikeActivity.this.getString(R.string.location_permission_is_required_for_this_app_to_work), Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permissions when the "Grant" button is clicked in the Snackbar
                        ActivityCompat.requestPermissions(HikeActivity.this, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Check if the permissions were granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted, proceed with requesting location updates
                requestLocationUpdates();
            } else {
                // Permissions are denied, handle this case (e.g., show an error message)
                progressCalculate.setVisibility(View.GONE);
                Toast.makeText(this, "Location permissions are required for this app to work.", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions are granted, proceed with requesting location updates
                Toast.makeText(this, "Location permissions are granted for this app to work.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Location permissions are not granted for this app to work.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initImagePickerNew() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {// Check if the selected image size exceeds the threshold (e.g., 1 MB)
                long imageSize = getImageSize(uri);
                long threshold = 1024 * 1024; // 1 MB threshold

                if (imageSize < threshold) {
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
                txtWarning.setVisibility(View.GONE);
                bitmapImageHike = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                imgHike.setVisibility(View.VISIBLE);
                imgHike.setImageBitmap(bitmapImageHike);
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
    private void showSnackbarCam() {
        Snackbar.make(findViewById(android.R.id.content), "Camera permission is required for this app to work.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Grant", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permissions when the "Grant" button is clicked in the Snackbar
                        ActivityCompat.requestPermissions(HikeActivity.this, new String[]{
                                Manifest.permission.CAMERA
                        }, CAMERA_PERMISSION_CODE);
                    }
                })
                .show();
    }

    private void initListener() {

        takeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(HikeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(HikeActivity.this, "Require camera permission!", Toast.LENGTH_LONG).show();
                    showSnackbarCam();
                }
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                imagePickerLauncher.launch(intent);
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtHikeName.setText("");
                edtHikeLength.setText("");
                edtDoH.setText("");
                edtLocation.setText("");
                edtDescription.setText("");
                bitmapImageHike = null;
                imgHike.setVisibility(View.GONE);
            }
        });

        edtHikeName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutHikeName.setError(null);
                }
            }
        });

        edtDoH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutHikeDate.setError(null);
                }
            }
        });

        edtLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutLocation.setError(null);
                }
            }
        });

        radioDifficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btnEasy) {
                    difficulty = 1;
                } else if (checkedId == R.id.btnModerate) {
                    difficulty = 2;
                } else if (checkedId == R.id.btnDifficult) {
                    difficulty = 3;
                }
            }
        });

        edtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    textInputLayoutDescription.setError(null);
                }
            }
        });

        radioParkingAvailable.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.btnParkingAvailable) {
                    parkingAvailable = true;
                } else if (checkedId == R.id.btnParkingUnavailable) {
                    parkingAvailable = false;
                }
            }
        });

        toolbarCreateHike.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnOpenMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HikeActivity.this, MapsActivity.class);
                intent.putExtra("activity","second");
                String hikeName = "";
                String location = "";
                double length = 0;
                String description= "";
                if (!edtHikeName.getText().toString().isEmpty()) {
                    hikeName = edtHikeName.getText().toString();
                }
                if (!edtHikeLength.getText().toString().isEmpty()) {
                    length = Double.parseDouble(edtHikeLength.getText().toString());
                }
                if (!edtLocation.getText().toString().isEmpty()) {
                    location = edtLocation.getText().toString();
                }
                if (!edtDescription.getText().toString().isEmpty()) {
                    description= edtDescription.getText().toString();
                }
                Hike hike = new Hike(hikeName, location, date, parkingAvailable, length, difficulty, description);
                HikeImage hikeImage = new HikeImage(bitmapImageHike);
                intent.putExtra(HIKE_KEY, hike);
                intent.putExtra(HIKE_IMAGE, hikeImage);
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

    private void confirm(){
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
        }else if (bitmapImageHike == null) {
            txtWarning.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please select an image!", Toast.LENGTH_SHORT).show();
        }else{
            txtWarning.setVisibility(View.GONE);
            String hikeName = edtHikeName.getText().toString();
            String location = edtLocation.getText().toString();
            double length = Double.parseDouble(edtHikeLength.getText().toString());
            if (!edtDescription.getText().toString().isEmpty()){
                description = edtDescription.getText().toString();
            }
            Hike hike = new Hike(hikeName, location, date, parkingAvailable, length, difficulty, description);
            HikeImage hikeImage = new HikeImage(bitmapImageHike);

            Intent intent = new Intent(HikeActivity.this, ConfirmActivity.class);
            intent.putExtra(HIKE_KEY, hike);
            intent.putExtra(HIKE_IMAGE, hikeImage);
            startActivity(intent);
//            edtHikeName.setText("");
//            edtLocation.setText("");
//            edtHikeLength.setText("");
//            edtDoH.setText("");
//            edtDescription.setText("");
//            bitmapImageHike=null;
//            imgHike.setVisibility(View.GONE);
        }

    }

    private void initView() {
        textInputLayoutHikeName = findViewById(R.id.textInputLayoutHikeName);
        textInputLayoutHikeLength = findViewById(R.id.textInputLayoutHikeLength);
        textInputLayoutHikeDate = findViewById(R.id.textInputLayoutHikeDate);
        textInputLayoutLocation = findViewById(R.id.textInputLayoutLocation);
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        progressCalculate = findViewById(R.id.progressCalculate);
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
        imgHike = findViewById(R.id.imgHike);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnClear = findViewById(R.id.btnClear);
        btnCreate = findViewById(R.id.btnCreate);
        btnOpenMap = findViewById(R.id.btnOpenMap);
        txtWarning = findViewById(R.id.txtWarning);
        toolbarCreateHike = findViewById(R.id.toolbarCreateHike);
        takeImage = findViewById(R.id.btnCaptureImage);
        db = HikeDatabase.getInstance(HikeActivity.this);
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
        Intent intent = new Intent(HikeActivity.this, MainActivity.class);
        intent.putExtra(SHOW_FRAGMENT, "hikeListFragment"); // Pass a unique identifier for the fragment
        startActivity(intent);
    }

}
