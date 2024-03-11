package com.geo.attendancesystm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPassword extends AppCompatActivity
{
    RelativeLayout relativeLayout;
    ImageView back;
    TextView num;
    EditText eteamil,etphone;
    String email,phone,phoneNum,content,id;
    CircleImageView imageView;
    Button sendEmail,verify;
    PrefManager preference;
    private static final String TAG = "ForgetPassword";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();

        relativeLayout.setVisibility(View.INVISIBLE);

        verify.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (valid1())
                {
                    if (phoneNum.equals(phone))
                    {
                        Intent intent = new Intent(getApplicationContext(),ResetPassword.class);
                        intent.putExtra("id",id);
                        intent.putExtra("content",content);
                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(ForgetPassword.this, "incorrect Number", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });
        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (valid())
                {
                    if (content.equals("student"))
                    {
                        checkEmailStudent(email);
                    }
                    else
                    if (content.equals("teacher"))
                    {
                        checkEmailTeacher(email);
                    }
                }
            }
        });
    }
    void init()
    {
        relativeLayout = findViewById(R.id.relative);
        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        back = findViewById(R.id.back);
        eteamil = findViewById(R.id.email);
        sendEmail = findViewById(R.id.sendEmail);
        etphone = findViewById(R.id.phone);
        num = findViewById(R.id.numberView);
        verify = findViewById(R.id.verify);

    }
    boolean valid()
    {
        email = eteamil.getText().toString().trim();
        if
        (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eteamil.setError("Please enter valid email address");
            return  false;
        }
        return true;
    }
    boolean valid1()
    {
        phoneNum = etphone.getText().toString().trim();
        if
        (phoneNum.isEmpty() || phoneNum.length()< 10)
        {
            etphone.setError("Please enter valid number");
            return  false;
        }
        return true;
    }
    void checkEmailTeacher(String email)
    {
        Call<StudentInfo> call = RetrofitClient.getInstance().getApi().checkEmailTeacher(email);
        call.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getResult().equals("success"))
                    {

                        StudentInfo studentInfo = response.body();
                        id = studentInfo.getId();
                        phone = studentInfo.getPhone();
                        relativeLayout.setVisibility(View.VISIBLE);
                        num.setText("Please enter your full Number "+phone.substring(phone.length()-4));

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Incorrect EMail and Password", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());

            }
        });

    }
    void checkEmailStudent(String email)
    {
        Call<StudentInfo> call = RetrofitClient.getInstance().getApi().checkEmailStudent(email);
        call.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {
                    StudentInfo studentInfo = response.body();
                    id = studentInfo.getId();
                    phone = studentInfo.getPhone();
                    relativeLayout.setVisibility(View.VISIBLE);
                    num.setText("Please enter your full Number "+phone.substring(phone.length()-4));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());

            }
        });
    }
}