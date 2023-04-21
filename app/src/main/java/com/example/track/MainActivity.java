package com.example.track;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final int GPS_TIME_INTERVAL = 1000; // get gps location every 1 second
    private static final int GPS_DISTANCE = 1000; // set the distance value in meter
    private static final int HANDLER_DELAY = 1000;
    private static final int START_HANDLER_DELAY = 0;
    public  static final int DEFAULT_UPDATE_INTERVAL=30;
    public  static final int FAST_UPDATE_INTERVAL=5;
    private static final int PERMISSION_FINE_LOCATION=99;

    private EditText name;
    private Button start;
    CreateUser createUser;
    LocationRequest locationRequest;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager =(LocationManager) getSystemService(LOCALE_SERVICE);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Create User");

        start=findViewById(R.id.start);
        name=findViewById(R.id.name);

        createUser = new CreateUser();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String poname= name.getText().toString();

                if(TextUtils.isEmpty(poname)){
                    Toast.makeText(MainActivity.this, "Please add some data.", Toast.LENGTH_SHORT).show();
                }else{
                    addDatatoFirebase(poname);
                }
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
        }
        Handler handler= new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                requestLocation();
                handler.postDelayed(this,1000);
            }
        },START_HANDLER_DELAY);
    }

    @Override
    public void onLocationChanged(Location location){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
        locationManager.removeUpdates(this);
    }

    public void requestLocation(){
        locationRequest = new LocationRequest();
        if(locationManager==null)
        {
            locationManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,GPS_TIME_INTERVAL,GPS_DISTANCE,this);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Handler handler= new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestLocation();
                    handler.postDelayed(this,HANDLER_DELAY);
                }
            },START_HANDLER_DELAY);
        }else
        {
            finish();
        }
    }

    private void addDatatoFirebase(String poname){
        createUser.setpolicename(poname);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(createUser);
                Toast.makeText(MainActivity.this, "data added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}