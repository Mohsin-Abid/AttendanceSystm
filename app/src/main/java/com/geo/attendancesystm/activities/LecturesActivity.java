package com.geo.attendancesystm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.geo.attendancesystm.Interfaces.ClickListenerInterface;
import com.geo.attendancesystm.R;
import com.geo.attendancesystm.adapters.LectureAdapter;
import com.geo.attendancesystm.model.classes.pojo.Lectures;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LecturesActivity extends AppCompatActivity implements ClickListenerInterface
{
    RecyclerView recyclerView;
    ImageView back;
    List<Lectures> list;
    LectureAdapter adp;
    private static final String TAG = "LecturesActivity";
    PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectures);
        init();
        fetchLectures();

    }
    void init()
    {
        recyclerView = findViewById(R.id.lecturesRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        back = findViewById(R.id.back);
        prefManager = new PrefManager(LecturesActivity.this);
    }

    void fetchLectures()
    {
        list = new ArrayList();
        Call<List<Lectures>> call = null;
        if (prefManager.getUserType().equals("student")){
            call = RetrofitClient.getInstance().getApi().get_lectures_students(prefManager.getUserId());
        }
        else if (prefManager.getUserType().equals("teacher")){
            call = RetrofitClient.getInstance().getApi().get_lectures_teachers(prefManager.getUserId());
        }
        call.enqueue(new Callback<List<Lectures>>() {
            @Override
            public void onResponse(Call<List<Lectures>> call, Response<List<Lectures>> response)
            {
                 if(response.isSuccessful())
                 {
                     list = response.body();
                     Log.d(TAG, "onResponse: "+new Gson().toJson(list));
                     if (!list.get(0).getResult().equals("0"))
                     {
                         adp = new LectureAdapter(LecturesActivity.this,list,LecturesActivity.this);
                         recyclerView.setAdapter(adp);
                     }
                 }
            }

            @Override
            public void onFailure(Call<List<Lectures>> call, Throwable t) {
                Log.d(TAG, "onFailure: "+t);
                Toast.makeText(LecturesActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void oniItemClick(int position) {

    }
}