package com.geo.attendancesystm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;
import com.geo.attendancesystm.model.geofencing.GeofenceHelper;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
{

    private static final String TAG = "DashboardActivity";
    CardView cardView1,cardView2,cardView3, cardView4;
    String content,address,name,phone,cnic,email,id,pic;
    TextView namee;
    ImageView imageView,menu;
    Button logoutbtn;
    PrefManager preference;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;
    LatLng latLng1 = new LatLng(31.4196071,73.1177892);


    private float GEOFENCE_RADIUS = 20;
    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_layout);

        init();
        getData();
        navigationView.getMenu().getItem(0).setChecked(true);
        menu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(GravityCompat.START);
                else drawerLayout.closeDrawer(GravityCompat.END);
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            }
        });
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), LecturesActivity.class);
                startActivity(intent);
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this,AddAttendance.class));
            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getApplicationContext(),LectureReports.class);
                startActivity(intent);

            }
        });
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                logout();
            }
        });


    }



    void mapsAdded()
    {
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        addMarker(latLng1);
        addCircle(latLng1, GEOFENCE_RADIUS);
        addGeofence(latLng1, GEOFENCE_RADIUS);
    }
    void getData()
    {
        preference = new PrefManager(getApplicationContext());
        content = preference.getUserType();
        id = preference.getUserId();

        if (content.equals("teacher"))
        {
            getTeacher(id);
        }
        if (content.equals("student"))
        {
            getStudent(id);
        }
    }
    void init()
    {
        preference = new PrefManager(getApplicationContext());
        imageView = findViewById(R.id.profilePic);
        namee = findViewById(R.id.userName);
        cardView1 = findViewById(R.id.card1);
        cardView2 = findViewById(R.id.card2);
        cardView3 = findViewById(R.id.card3);
        cardView4 = findViewById(R.id.card4);
        logoutbtn = findViewById(R.id.logoutBtn);
        menu = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);
    }
    void logout()
    {
        Toast.makeText(getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
        preference.setFaceAdded("");
        preference.setPassword("");
        preference.setUserPhone("");
        preference.setUserEmail("");
        preference.setUserName("");
        preference.setUserId("");
        preference.setUserType("");
        startActivity(new Intent(DashboardActivity.this,MainActivity.class));
        finish();
    }


    /////

    //working on geofencing


    private void addMarker(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);
    }

    private void addCircle(LatLng latLng, float radius) {
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }
    void getStudent(String id)
    {
        Call<StudentInfo> call1 = RetrofitClient.getInstance().getApi().getStudent(id);
        call1.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response)
            {
                if (response.isSuccessful())
                {
                    StudentInfo studentInfo = response.body();
                    name =studentInfo.getName();
                    pic = studentInfo.getProfilepicture();
                    Log.d(TAG, "onResponse: nomi"+pic);
                    setData(name,pic);
                }

            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(DashboardActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onFailure:student "+t.getMessage());
            }
        });
    }
    void getTeacher(String id)
    {
        Log.d(TAG, "getTeacher: id "+id);
        Call<StudentInfo> call1 = RetrofitClient.getInstance().getApi().getTeacher(id);
        call1.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response)
            {
                if (response.isSuccessful())
                {
                    StudentInfo studentInfo = response.body();
                    name =studentInfo.getName();
                    pic = studentInfo.getProfilepicture();
                    Log.d(TAG, "onResponse: nomi "+pic);
                    setData(name,pic);
                }
            }
            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(DashboardActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: mohsiin "+t.getMessage());
                Log.d(TAG, "id: "+id);
            }
        });
    }
    void setData( String name, String profilepicture)
    {
        namee.setText(name);
        Picasso.get().load(RetrofitClient.BASE_URL+profilepicture).into(imageView);
        Log.d(TAG, "setData: "+imageView);

        View headerView = navigationView.getHeaderView(0);
        TextView navText = (TextView) headerView.findViewById(R.id.text) ;
        ImageView img = (ImageView) headerView.findViewById(R.id.img);
        navText.setText(name);
        Picasso.get().load(RetrofitClient.BASE_URL+profilepicture).into(img);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id =item.getItemId();
        switch (id){
            case R.id.home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                Intent intent = new Intent(getApplicationContext(),UpdatePassword.class);
                intent.putExtra("pic",pic);
                startActivity(intent);

                break;
            case R.id.feedback:
                Toast.makeText(this, "yess comande "+item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(this, "yess comande "+item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                logout();
                break;

        }
        return false;
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }
}