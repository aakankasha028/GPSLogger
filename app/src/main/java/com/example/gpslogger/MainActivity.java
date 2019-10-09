package com.example.gpslogger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener{

    private static final String TAG = "My App";
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private List<String[]> locationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void SetLatLongSpeed(View view) {

        boolean gps_enabled = false;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location bestLocation = null;
        Integer logcnt = 1;

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 0, this);

            if (gps_enabled) {
                List<String> providers = lm.getProviders(true);
                for (String provider : providers) {
                    Location l = lm.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }

                if (bestLocation == null) {
                    Toast.makeText(MainActivity.this, "ERR: Unable to find location", Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(MainActivity.this, "Started logging", Toast.LENGTH_SHORT).show();
                    updateTextView(bestLocation);
                }

            } else {        // ask user to enable GPS
                Toast.makeText(this, "Please enable GPS for logging", Toast.LENGTH_LONG).show();
            }
        } else {        //request permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void updateTextView(Location bestLocation ) {
        Log.i(TAG, "Updating TextView(s)");

        TextView latitudeValue = findViewById(R.id.latitudeValue);
        TextView longitudeValue = findViewById(R.id.longitudeValue);
        TextView speedValue = findViewById(R.id.speedValue);

        latitudeValue.setText(String.format("%f", bestLocation.getLatitude()));
        longitudeValue.setText(String.format("%f", bestLocation.getLongitude()));
        speedValue.setText(String.format("%f", bestLocation.getSpeed()));

        String[] locationToStringArr = {Double.toString(bestLocation.getLatitude()),
                                        Double.toString(bestLocation.getLongitude()),
                                        Double.toString(bestLocation.getSpeed())};
        locationList.add(locationToStringArr);
    }

    public void logDetailsIntoCSV(View view) {
        String FILENAME = "/Log" + new Date().toString() + ".csv";
        Log.i(TAG ,"logging to internal storage : " + FILENAME);

        String[] header = {"Latitude","Longitude","Speed"};
        File externalFilesDir = new File(getExternalFilesDir(null) + FILENAME);
        Writer writer = null;
        try {
            writer = new FileWriter(externalFilesDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);


        csvWriter.writeNext(header);
        for (String[] location : locationList) {
            csvWriter.writeNext(location);
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Logging completed");

        Toast.makeText(this, "Stored files successfully at " + FILENAME, Toast.LENGTH_SHORT).show();
        locationList.clear();

    }

    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Updating details", Toast.LENGTH_SHORT).show();
        if (location != null) {
//            Toast.makeText(this, "Updating details", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "requested location updates");
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Log.i("Location Changed", location.getLatitude() + " and " + location.getLongitude());
            updateTextView(location);
//            lm.removeUpdates(this);
        }
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        ;
    }

    public void onProviderEnabled(String s) {
        ;
    }

    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
    }


    public void viewPointsOnMap (View view) {
        // list all files on another activity
        Intent intent = new Intent(this, CSVList.class);
        startActivity(intent);
    }

}
