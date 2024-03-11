package com.geo.attendancesystm.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geo.attendancesystm.R;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;
import com.geo.attendancesystm.model.preference.PrefManager;
import com.geo.attendancesystm.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity
{
    EditText epass,erepass;
    String pass,repass,content,id;
    Button updateBtn;
    private static final String TAG = "ResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        init();

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValid())
                {

                    if (content.equals("teacher"))
                    {
                        teacherPass(id,pass);
                    }
                    else if (content.equals("student"))
                    {
                        studentPass(id,pass);
                    }
                }
            }
        });

    }
    void  init()
    {

        Intent intent = getIntent();
        content = intent.getStringExtra("content");
        id = intent.getStringExtra("id");
        updateBtn = findViewById(R.id.updateBtn);
        epass = findViewById(R.id.pass);
        erepass = findViewById(R.id.rePass);


    }
    void gettingStrings()
    {
        pass = epass.getText().toString();
        repass = erepass.getText().toString();
    }
    boolean isValid() {
        gettingStrings();

        if (pass.isEmpty() || pass.length() < 6) {
            epass.setError("Password must be 6 letter");
            return false;
        }
        if (repass.isEmpty() || !repass.equals(pass)) {
            erepass.setError("Password not matched ");
            return false;
        }
        return true;
    }
    void teacherPass(String id, String password)
    {
        Call<StudentInfo> call1 = RetrofitClient.getInstance().getApi().passwordTeacher(id,password);
        call1.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response)
            {
                if (response.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Reset password successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.putExtra("content",content);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });
    }
    void studentPass(String id, String password)
    {
        Call<StudentInfo> call1 = RetrofitClient.getInstance().getApi().passwordStudent(id,password);
        call1.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response)
            {
                if (response.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "Reset password successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    intent.putExtra("content",content);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(getApplicationContext(), "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
            }
        });

    }
}