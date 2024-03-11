package com.geo.attendancesystm.model.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager
{
    public static final String PREF_NAME = "Booking";
    private Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static final String NAME = "name";
    public static final String EMAIL ="email";
    public static final String PHONE ="phone";
    public static final String id = "id";
    private static final String ALREADY_SIGNIN = "already";
    private static final String PASSWORD = "password";
    public static final String FACE_ADDED = "face added";
    public static final String TYPE = "type";
    public static final String FACE = "face";

    public PrefManager(Context context)
    {
        context = context;
        preferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setUserName(String key)
    {
        editor.putString(NAME,key);
        editor.commit();
    }
    public String getUserName()
    {
        return preferences.getString(NAME,null);
    }
    public void setFace(String key)
    {
        editor.putString(FACE,key);
        editor.commit();
    }
    public String getFace()
    {
        return preferences.getString(FACE,null);
    }


    public void setUserType(String key)
    {
        editor.putString(TYPE,key);
        editor.commit();
    }
    public String getUserType()
    {
        return preferences.getString(TYPE,"");
    }

    public void setFaceAdded(String key)
    {
        editor.putString(FACE_ADDED,key);
        editor.commit();
    }
    public String getFaceAdded()
    {
        return preferences.getString(FACE_ADDED,null);
    }
    public String getUserEmail()
    {
        return preferences.getString(EMAIL,null);
    }
    public void setUserEmail(String key)
    {
        editor.putString(EMAIL,key);
        editor.commit();
    }
    public void setUserPhone(String key)
    {
        editor.putString(PHONE,key);
        editor.commit();
    }
    public String getUserPhone()
    {
        return preferences.getString(PHONE,null);
    }
    public void setUserId(String key)
    {
        editor.putString(id,key);
        editor.commit();
    }
    public String getUserId()
    {
        return preferences.getString(id,null);
    }

    public String getPassword()
    {
        return preferences.getString(PASSWORD,null);
    }
    public void setPassword(String key)
    {
        editor.putString(PASSWORD,key);
        editor.commit();
    }
    public void setAlreadySignin(Boolean key){
        editor.putBoolean(ALREADY_SIGNIN,key);
        editor.commit();
    }


    public Boolean getAlreadySignin()
    {
        return preferences.getBoolean(ALREADY_SIGNIN,false);
    }


}
