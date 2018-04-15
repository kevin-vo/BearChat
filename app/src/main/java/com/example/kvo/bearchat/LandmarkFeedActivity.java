package com.example.kvo.bearchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class LandmarkFeedActivity extends AppCompatActivity {

    String username;
    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Landmark> mLandmarks = new ArrayList<Landmark>();

    CardView mCardView;
    TextView landmarkText;
    Toolbar landmarkToolbar;

    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private final int REQUEST_FINE_LOCATION = 1234;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_feed);

        username = (String) getIntent().getExtras().get("usernameString");

        mRecyclerView = (RecyclerView) findViewById(R.id.landmark_recycler);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        mCardView = (CardView) findViewById(R.id.cardview);
        landmarkText = (TextView) findViewById(R.id.landmark_text);
        landmarkToolbar = (Toolbar) findViewById(R.id.landmark_toolbar);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        setSupportActionBar(landmarkToolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }

        generateLandmarks();
        setAdapterAndUpdateData();

    }



    private void updateLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
            long FASTEST_INTERVAL = 2000; /* 2 sec */
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);

            mFusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    // do work here
                    onLocationChanged(locationResult.getLastLocation());
                }
            }, Looper.myLooper());






            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                mCurrentLocation = location;


                                for (Landmark landmark : mLandmarks) {
                                    Location l = new Location("");
                                    l.setLatitude(landmark.getLatitude());
                                    l.setLongitude(landmark.getLongitude());
                                    landmark.setDistance(mCurrentLocation.distanceTo(l));
                                }
                                mRecyclerView.setAdapter(mAdapter);

                            }
                        }
                    });
        }

    }

    public void onLocationChanged(Location location) {
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.landmark_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_sign_out) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent logInIntent = new Intent(LandmarkFeedActivity.this, LoginActivity.class);
            logInIntent.addFlags(logInIntent.FLAG_ACTIVITY_CLEAR_TOP | logInIntent.FLAG_ACTIVITY_NEW_TASK);
            LandmarkFeedActivity.this.startActivity(logInIntent);
            return true;
        } else {
            updateLocation();
            if (mCurrentLocation != null) {
                Toast.makeText(this, "Location updated!", Toast.LENGTH_SHORT).show();
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
                }
                Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();

            }
            return true;
        }
    }

    private void setAdapterAndUpdateData() {
        updateLocation();
        LandmarkAdapter l = new LandmarkAdapter(this, mLandmarks);
        l.setUsername(username);
        mAdapter = l;
        mRecyclerView.setAdapter(mAdapter);
    }

    private void generateLandmarks() {
        Landmark q = new Landmark("Class of 1927 Bear",
                37.869288,
                -122.260125,
                -1,
                R.drawable.mlk_bear);
        Landmark w = new Landmark("Stadium Entrance Bear",
                37.871305,
                -122.252516,
                -1,
                R.drawable.outside_stadium);
        Landmark e = new Landmark("Macchi Bears",
                37.874118,
                -122.258778,
                -1,
                R.drawable.macchi_bears);
        Landmark r = new Landmark("Les Bears",
                37.871707,
                -122.253602,
                -1,
                R.drawable.les_bears);
        Landmark t = new Landmark("Strawberry Creek Topiary Bear",
                37.869861,
                -122.261148,
                -1,
                R.drawable.strawberry_creek);
        Landmark y = new Landmark("South Hall Little Bear",
                37.871382,
                -122.258355,
                -1,
                R.drawable.south_hall);
        Landmark u = new Landmark("Great Bear Bell Bears",
                37.872061599999995,
                -122.2578123,
                -1,
                R.drawable.bell_bears);
        Landmark i = new Landmark("Campanile Esplanade Bears",
                37.87233810000001,
                -122.25792999999999,
                -1,
                R.drawable.bench_bears);
        mLandmarks.add(q);
        mLandmarks.add(w);
        mLandmarks.add(e);
        mLandmarks.add(r);
        mLandmarks.add(t);
        mLandmarks.add(y);
        mLandmarks.add(u);
        mLandmarks.add(i);


    }

}
