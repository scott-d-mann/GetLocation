package com.example.getlocation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button getLocationBtn;
    TextView longTV, latTV;
    //debug variable
    String TAG = "MActivity";
    //Provides the entry point to the Fused Location Provider API.
    private FusedLocationProviderClient fusedLocationClient;
    //Represents a geographical location.
    protected Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationBtn = (Button)findViewById(R.id.getLocationBtn);
        longTV = (TextView) findViewById(R.id.longTV);
        latTV = (TextView) findViewById(R.id.latTV);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check we have permission
                if (!hasPermission())
                {
                    //ask permissions
                    Log.d(TAG, "Asking Real time permission");
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
                } else {
                    Log.d(TAG, "Getting Location");
                    getLastLocation();
                }

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.d(TAG, "Lat: " + location.getLatitude());
                            Log.d(TAG, "Long: " + location.getLongitude());
                            latTV.setText(String.format(Locale.ENGLISH, "%f",
                                    location.getLatitude()));
                            longTV.setText(String.format(Locale.ENGLISH, "%f",
                                    location.getLongitude()));
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"no_location_detected",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Location obj is null");
                        }
                    }
                });
    }

    private boolean hasPermission() {
        //return the permission
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    //we have permission lets get location
                    getLastLocation();
                } else {
                    // Send a toast message to the user telling them they will not be able to use the app without allowing fine location permission.
                    // There are multiple ways to do this, view the textbook for more information https://ebookcentral-proquest-com.ez.library.latrobe.edu.au/lib/latrobe/reader.action?docID=6642493&ppg=631
                    Toast.makeText(this, "Essential permission not granted please restart the app to try again", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Permission not granted");
                }
            });

}


