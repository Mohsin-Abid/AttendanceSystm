package com.geo.attendancesystm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.attendancesystm.Interfaces.ClickListenerInterface;
import com.geo.attendancesystm.R;
import com.geo.attendancesystm.adapters.LectureAdapter;
import com.geo.attendancesystm.adapters.ReportsAdapter;
import com.geo.attendancesystm.model.classes.pojo.Lectures;
import com.geo.attendancesystm.model.classes.pojo.ReportModel;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Reports extends AppCompatActivity implements ClickListenerInterface
{
    RecyclerView recyclerView;
    ImageView back;
    private static final String TAG = "Report";
    List<ReportModel> list;
    ReportsAdapter adp;
    PrefManager prefManager;
    String subject,sClass,id;
    CircularSeekBar seekbar;
    TextView sClassTV,subjectTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        init();
        getAllIntent();
        fetchData();
    }
    void init()
    {
        back = findViewById(R.id.back);
        prefManager = new PrefManager(this);
        seekbar = findViewById(R.id.seekbar);
        sClassTV = findViewById(R.id.sClass);
        subjectTV = findViewById(R.id.subject);
    }
    void getAllIntent(){
        Intent i = getIntent();
        id = i.getStringExtra("id");
        subject = i.getStringExtra("subject");
        sClass = i.getStringExtra("class");
        sClassTV.setText(sClass);
        subjectTV.setText(subject);
    }
    void fetchData(){
        list = new ArrayList<>();
        Call<ReportModel> call = RetrofitClient.getInstance().getApi()
                .getAllReports(prefManager.getUserId(),prefManager.getUserType(),id);
        call.enqueue(new Callback<ReportModel>() {
            @Override
            public void onResponse(Call<ReportModel> call, Response<ReportModel> response) {
                if (response.isSuccessful()){
                    ReportModel m = response.body();
                    // set seek bar data
                    seekbar.setProgress(m.getPresent());
                    seekbar.setMax(m.getTotal());
                    Log.d(TAG, "onResponse: "+m.getPresent());
                    Log.d(TAG, "onResponse: "+m.getTotal());
                }
                else{
                    Log.d(TAG, "onResponse: isNotSuccessful");
                }
            }

            @Override
            public void onFailure(Call<ReportModel> call, Throwable t) {
                Toast.makeText(Reports.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void oniItemClick(int position) {

    }
}