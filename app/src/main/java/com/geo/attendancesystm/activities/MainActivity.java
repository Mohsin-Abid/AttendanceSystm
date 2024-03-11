package com.geo.attendancesystm.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 101;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 102;
    private static final int CAMERA_PERMISSION_CODE = 105;
    private static final int ERROR_DIALOG_REQUEST = 103;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private boolean mLocationPermissionGranted = false;

    private static final String TAG = "MainActivity";


    int a = 0,b=0;
    CardView  student, teacher;
    ImageView next;
    String content;
    PrefManager preference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        if (!preference.getUserType().isEmpty())
        {
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        checkMapServices();
        checkAndRequestPermissions();
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a==0)
                {
                    a=1;
                    b=0;
                    student.setCardBackgroundColor(getResources().getColor(R.color.blue));
                    teacher.setCardBackgroundColor(getResources().getColor(R.color.theme));
                    content = "student";
                    next.setVisibility(View.VISIBLE);
                }
                else
                {
                    a=0;
                    b=0;
                    student.setCardBackgroundColor(getResources().getColor(R.color.theme));
                    content = "";
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });
        teacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b==0)
                {
                    b=1;
                    a=0;
                    teacher.setCardBackgroundColor(getResources().getColor(R.color.blue));
                    student.setCardBackgroundColor(getResources().getColor(R.color.theme));
                    content = "teacher";
                    next.setVisibility(View.VISIBLE);
                }
                else
                {
                    b=0;
                    a=0;
                   // teacher.setCardBackgroundColor(getColor(R.color.card));
                    teacher.setCardBackgroundColor(getResources().getColor(R.color.theme));
                    content = "";
                    next.setVisibility(View.INVISIBLE);
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("content",content);
                startActivity(intent);
                finish();
            }
        });
    }
    void init()
    {
        student = findViewById(R.id.studentCard);
        teacher = findViewById(R.id.teacherCard);
        preference = new PrefManager(getApplicationContext());
        next = findViewById(R.id.next);

    }




    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }

                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }
    public boolean isServicesOK()
    {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable
                (MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog
                    (MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    public void checkAndRequestPermissions()
    {
        int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (write != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (read != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (read != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (read != PackageManager.PERMISSION_GRANTED)
        {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (!listPermissionsNeeded.isEmpty())
        {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), STORAGE_PERMISSION_CODE);
        }else
        {
            Toast.makeText(this, "done" , Toast.LENGTH_SHORT).show();
        }
    }
}