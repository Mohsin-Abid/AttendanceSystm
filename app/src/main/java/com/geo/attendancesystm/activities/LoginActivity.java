package com.geo.attendancesystm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
{

    private static final String TAG = "LoginActivity";
    EditText loginemail,loginpassword;
    Button button;
    String email,password,content;
    PrefManager preference;
    TextView forget;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getData();
        init();



        if(preference.getUserId() != null && preference.getFaceAdded().equals("face ok") )
        {
            Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(intent);
            finish();
        }
        else if (preference.getUserId()!= null && preference.getFaceAdded().equals("update ok"))
        {
            Intent intent = new Intent(getApplicationContext(),AddFace.class);
            startActivity(intent);
            finish();

        }
        else if (preference.getUserId()!= null && preference.getFaceAdded().equals("login ok"))
        {
            Intent intent = new Intent(getApplicationContext(), UpdateInfo.class);
            startActivity(intent);
            finish();
        }


        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgetPassword.class);
                intent.putExtra("content",content);
                startActivity(intent);
                finish();
            }
        });
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (isValid())
                {
                    gettingStrings();
                    if (content.equals("student"))
                    {
                        loginStudent(email,password);

                    }
                    else if (content.equals("teacher"))
                    {
                        loginSTeacher(email,password);

                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    void getData ()
    {
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        Log.d(TAG, "init: "+content);
    }
    void init()
    {
        forget = findViewById(R.id.forget);
        preference = new PrefManager(getApplicationContext());
        button = findViewById(R.id.loginBtn);
        loginemail = findViewById(R.id.loginEmail);
        loginpassword = findViewById(R.id.loginPassword);

    }
    void gettingStrings()
    {
        email = loginemail.getText().toString().trim();
        password = loginpassword.getText().toString();
    }
    boolean isValid()
    {
        gettingStrings();
        if
        (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginemail.setError("Please enter valid email address");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            loginpassword.setError("At least 6 digit password");
            return false;
        }

        return true;
    }

    public void loginStudent(String email,String password)
    {
        Call<StudentInfo> call = RetrofitClient.getInstance().getApi().loginStudent(email,password);
        call.enqueue(new Callback<StudentInfo>() 
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getResult().equals("success"))
                    {

                        StudentInfo studentInfo = response.body();
                        if (studentInfo.getFace_state() == null || studentInfo.getFace_state().isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Successfully login ", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onResponse: isSuccessful" + response + new Gson().toJson(studentInfo));
                            preference.setUserId(studentInfo.getId());
                            preference.setUserName(studentInfo.getName());
                            preference.setUserEmail(studentInfo.getEmail());
                            preference.setUserPhone(studentInfo.getPhone());
                            preference.setFace(studentInfo.getFace_state());
                            preference.setUserType("student");
                            preference.setFaceAdded("login ok");
                            Intent intent = new Intent(LoginActivity.this, UpdateInfo.class);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            preference.setUserId(studentInfo.getId());
                            preference.setUserName(studentInfo.getName());
                            preference.setUserEmail(studentInfo.getEmail());
                            preference.setUserPhone(studentInfo.getPhone());
                            preference.setFace(studentInfo.getFace_state());
                            preference.setUserType("student");
                            preference.setFaceAdded("face ok");
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Incorrect EMail and Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(LoginActivity.this, "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());

            }
        });
    }
    public void loginSTeacher(String email,String password)
    {
        Call<StudentInfo> call = RetrofitClient.getInstance().getApi().loginTeacher(email,password);
        call.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getResult().equals("success"))
                    {
                        StudentInfo studentInfo = response.body();
                        if (studentInfo.getFace_state() == null || studentInfo.getFace_state().isEmpty()) {
                            Log.d(TAG, "onResponse: isSuccessful" + response + new Gson().toJson(studentInfo));
                            Toast.makeText(LoginActivity.this, "Successfully login ", Toast.LENGTH_SHORT).show();
                            preference.setUserId(studentInfo.getId());
                            preference.setUserName(studentInfo.getName());
                            preference.setUserEmail(studentInfo.getEmail());
                            preference.setUserPhone(studentInfo.getPhone());
                            preference.setPassword(studentInfo.get_password());
                            preference.setUserType("teacher");
                            preference.setFaceAdded("login ok");
                            startActivity(new Intent(LoginActivity.this, UpdateInfo.class));
                            finish();
                        }
                        else
                        {
                            preference.setUserId(studentInfo.getId());
                            preference.setUserName(studentInfo.getName());
                            preference.setUserEmail(studentInfo.getEmail());
                            preference.setUserPhone(studentInfo.getPhone());
                            preference.setFace(studentInfo.getFace_state());
                            preference.setUserType("teacher");
                            preference.setFaceAdded("face ok");
                            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));
                            finish();
                        }
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Incorrect EMail and Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(LoginActivity.this, "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(LoginActivity.this, "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }

}