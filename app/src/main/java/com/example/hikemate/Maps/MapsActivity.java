package com.example.hikemate.Maps;

import static com.example.hikemate.Hike.HikeActivity.HIKE_IMAGE;
import static com.example.hikemate.Hike.HikeDetail.HIKE_KEY;
import static com.example.hikemate.WeatherForecast.WeatherActivity.REQUEST_CHECK_SETTINGS;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.fragment.app.FragmentContainerView;

import com.example.hikemate.Database.HikeDatabase;
import com.example.hikemate.Database.Model.Hike;
import com.example.hikemate.Database.Model.HikeImage;
import com.example.hikemate.Hike.HikeActivity;
import com.example.hikemate.Hike.HikeDetail;
import com.example.hikemate.NetworkUtils;
import com.example.hikemate.R;
import com.example.hikemate.WeatherForecast.WeatherActivity;
import com.example.hikemate.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String LATLNG_KEY = "latlng_key";
    private GoogleMap mMap;
    private static final String TAG = "MapsActivity";
    private PlacesClient mPlacesClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private Location mLastKnownLocation;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted, gpsPermission;
    private MaterialToolbar toolbar;
    private NetworkUtils networkUtils;
    private ConstraintLayout lostLayout;
    private HikeDatabase db;
    private FloatingActionButton btnLocation;
    private LocationCallback locationCallback = new LocationCallback() {
    };
    private CircularProgressIndicator loading;

    private Handler handler = new Handler();

    // Define a Runnable to be executed after the delay
    private Runnable sleepRunnable = new Runnable() {
        @Override
        public void run() {
            // This code will be executed after the delay (sleep)
            loading.setVisibility(View.GONE);
            btnLocation.setVisibility(View.VISIBLE);
            getDeviceLocation();
        }
    };

    private FragmentContainerView map;

    private SearchView mapSearch;
    private LatLng latLngPass;
    private boolean showAgain = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getSupportActionBar().hide();
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        initView();
        initMap();
        initListener();
    }

    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
        // Initialize the Places client
        String apiKey = getString(R.string.google_map_key);
        Places.initialize(getApplicationContext(), apiKey);
        mPlacesClient = Places.createClient(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void refreshLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000).build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getLocationPermission();
            return;
        }

        // Initialize the LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    mLastKnownLocation = locationResult.getLastLocation();
                }
            }
        };

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    private void initView() {
        // Set up the action toolbar
        toolbar = findViewById(R.id.toolbar);
        btnLocation = findViewById(R.id.btnLocation);
        loading = findViewById(R.id.loading);
        map = findViewById(R.id.map);
        mapSearch = findViewById(R.id.mapSearch);
        db = HikeDatabase.getInstance(MapsActivity.this);
    }

    private void initListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.confirm) {
                    //Get activity
                    Intent intent = getIntent();
                    String activity = intent.getStringExtra("activity");
                    Intent intent2 = null;
                    if (activity.equals("first")) {
                        intent2 = new Intent(MapsActivity.this, WeatherActivity.class);
                    } else if (activity.equals("second")) {
                        intent2 = new Intent(MapsActivity.this, HikeActivity.class);
                        intent2.putExtra(HIKE_KEY, (Hike) intent.getParcelableExtra(HIKE_KEY));
                        intent2.putExtra(HIKE_IMAGE, (HikeImage) intent.getParcelableExtra(HIKE_IMAGE));
                    } else if (activity.equals("third")) {
                        intent2 = new Intent(MapsActivity.this, HikeDetail.class);
                        intent2.putExtra(HIKE_KEY, (Hike) intent.getParcelableExtra(HIKE_KEY));
                    }
                    intent2.putExtra(LATLNG_KEY, latLngPass);
                    startActivity(intent2);
                    return true;
                }
                return false; // Return false to indicate that the event has not been handled
            }
        });

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });

        mapSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = query;
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    mMap.clear();
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    latLngPass = latLng;
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    Toast.makeText(getApplicationContext(), address.getLatitude() + " " + address.getLongitude(), Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }


    private void requestLocationUpdates() {
        gpsPermission = false;
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
                        gpsPermission = true;
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
                                resolvable.startResolutionForResult(MapsActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sendEx) {
                                // Ignore the error.
                            }
                        } else {
                            // Location settings are not satisfied, and cannot be resolved directly.
                            // Handle this case appropriately (e.g., show an error message).
                            Toast.makeText(MapsActivity.this, "Location settings are not satisfied.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gpsPermission = false;
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                // User enabled location settings. Request location updates with permission.
                gpsPermission = true;
                refreshLocation();
                loading.setVisibility(View.VISIBLE);
                btnLocation.setVisibility(View.GONE);
                sleep(5000);
                Toast.makeText(this, "It will take a while to get your location!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the zoom controls for the map
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Handle the map click event here
                placeMarker(latLng);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Remove the marker when clicked
//                marker.remove();
                marker.showInfoWindow();
                return true;
            }
        });
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Prompt the user for permission.
        getLocationPermission();

        mMap.clear();

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, Integer.MAX_VALUE).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && locationResult.getLastLocation() != null) {
                    mLastKnownLocation = locationResult.getLastLocation();
                    if (mLastKnownLocation != null) {
                        // Set the map's camera position to the current location of the device.
                        Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                        Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        latLngPass = latLng;
                        mMap.addMarker(new MarkerOptions()
                                .title(reverseGeocodeName(latLng))
                                .position(latLng)
                                .snippet(reverseGeocodeAddress(latLng)));
                    } else {
                        Toast.makeText(MapsActivity.this, "Getting your location! Press again after a few seconds", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }




    private String reverseGeocodeName(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String locationName = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                locationName = address.getFeatureName(); // Get the location name

                // Print or display the location name and address
                Log.d("LocationInfo", "Location Name: " + locationName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationName;
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


    private void placeMarker(LatLng latLng) {
        // Clear any existing markers
        mMap.clear();

        // Add a marker at the clicked location
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(reverseGeocodeName(latLng))
                .snippet(reverseGeocodeAddress(latLng)));

        latLngPass = latLng;


        // Optionally, move the camera to the clicked location
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
    }


    private void getLocationPermission() {
        mLocationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            requestLocationUpdates();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                requestLocationUpdates();
                refreshLocation();
                loading.setVisibility(View.VISIBLE);
                btnLocation.setVisibility(View.GONE);
                sleep(5000);
            }
        }
    }

    private void getDeviceLocation() {
        if (mMap != null)
            mMap.clear();
        try {
            if (mLocationPermissionGranted && gpsPermission) {
                if (mLastKnownLocation != null) {
                    // Set the map's camera position to the current location of the device.
                    Log.d(TAG, "Latitude: " + mLastKnownLocation.getLatitude());
                    Log.d(TAG, "Longitude: " + mLastKnownLocation.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    latLngPass = latLng;
                    mMap.addMarker(new MarkerOptions()
                            .title(reverseGeocodeName(latLng))
                            .position(latLng)
                            .snippet(reverseGeocodeAddress(latLng)));
                } else {
                    Toast.makeText(this, "Getting your location! Press again after a few seconds", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestLocationUpdates();
            }
        } catch (SecurityException e) {
            Log.e("Something wrong: %s", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void sleep(int milliseconds) {
        handler.postDelayed(sleepRunnable, milliseconds);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationProviderClient != null) {
            mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

}