package edu.gcu.gpstrackingdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int DEFAULT_UPDATE_INTERVAL = 30;
    private static final int FAST_UPDATE_INTERVAL = 5;
    private static final int PERMISSIONS_FINE_LOCATION = 1;

    TextView
            tv_lat,
            tv_labellon,
            tv_lon,
            tv_labelaltitude,
            tv_altitude,
            tv_labelaccuracy,
            tv_accuracy,
            tv_labelspeed,
            tv_speed,
            tv_labelsensor,
            tv_sensor,
            tv_labelupdates,
            tv_updates,
            tv_address,
            tv_lbladdress;
    Switch
            sw_locationsupdates,
            sw_gps;
    private LocationRequest locationRequest;
    private LocationCallback locationCallBack;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_lat = findViewById(R.id.tv_lat);
        tv_labellon = findViewById(R.id.tv_labellon);
        tv_lon = findViewById(R.id.tv_lon);
        tv_labelaltitude = findViewById(R.id.tv_labelaltitude);
        tv_altitude = findViewById(R.id.tv_altitude);
        tv_labelaccuracy = findViewById(R.id.tv_labelaccuracy);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_labelspeed = findViewById(R.id.tv_labelspeed);
        tv_speed = findViewById(R.id.tv_speed);
        tv_labelsensor = findViewById(R.id.tv_labelsensor);
        tv_sensor = findViewById(R.id.tv_sensor);
        tv_labelupdates = findViewById(R.id.tv_labelupdates);
        tv_updates = findViewById(R.id.tv_updates);
        tv_address = findViewById(R.id.tv_address);
        tv_lbladdress = findViewById(R.id.tv_lbladdress);
        sw_locationsupdates = findViewById(R.id.sw_locationsupdates);
        sw_gps = findViewById(R.id.sw_gps);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(1000 * FAST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        sw_gps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sw_gps.isChecked()) {
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    tv_sensor.setText("Using GPS sensors");
                } else {
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                    tv_sensor.setText("Using Towers  +  WiFi");
                }
            }
        });

        sw_locationsupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationsupdates.isChecked()) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                }
            }
        });
        updateGPS();
    }

    private void startLocationUpdates() {
        tv_updates.setText("Location is NOT being tracked");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);
        } else {
            //permissions not granted yt
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
        updateGPS();
    }

    private void stopLocationUpdates() {
        tv_updates.setText("Location is being tracked");
        tv_lat.setText("Not Tracking location");
        tv_lon.setText("Not Tracking location");
        tv_speed.setText("Not Tracking location");
        tv_address.setText("Not Tracking location ");
        tv_accuracy.setText("Not Tracking location");
        tv_altitude.setText("Not Tracking location");
        tv_sensor.setText("Not Tracking location");
        fusedLocationProviderClient.removeLocationUpdates(locationCallBack);
    }

    private void updateGPS() {
        // get permissions from the user to track GPS
        // get the current location from the fused client
        // update the UI - i.e set all properties in their associated text view items;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we go permissions. Put the values of location. XXX into the UI component.
                    updateUIValues(location);
                }
            });
        } else {
            //permissions not granted yt
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                } else {
                    Toast.makeText(this, "This app permissions to be granted in order to work properly", Toast.LENGTH_LONG);
                    finish();
                }
        }
    }

    private void updateUIValues(Location location) {
        // update all of the text view object with a new location
        tv_lat.setText(String.valueOf(location.getLatitude()));
        tv_lon.setText(String.valueOf(location.getLongitude()));
        tv_accuracy.setText(String.valueOf(location.getAccuracy()));
        if (location.hasAltitude()) {
            tv_altitude.setText(String.valueOf(location.getAltitude()));
        } else {
            tv_altitude.setText("Not available");
        }

        if (location.hasSpeed()) {
            tv_speed.setText(String.valueOf(location.getSpeed()));
        } else {
            tv_speed.setText("Not available");
        }
        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tv_address.setText(addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_address.setText(addresses.get(0).getAddressLine(0));
    }

}