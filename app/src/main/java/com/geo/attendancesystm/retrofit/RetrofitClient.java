package com.geo.attendancesystm.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient
{
//    public static String BASE_URL="http://192.168.18.49/lms_system/";
//    public static String BASE_URL="http://192.168.100.5/lms_system/";
    public static String BASE_URL="https://lms.dpsquiz.xyz/";
    public static RetrofitClient mInstance;
    public static Retrofit retrofit;

    private RetrofitClient()
    {
        Gson gson = new GsonBuilder().setLenient().create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance()
    {
        if (mInstance ==  null)
        {
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}

