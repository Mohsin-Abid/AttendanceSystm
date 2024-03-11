package com.geo.attendancesystm.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    EditText uEmail,uAddress,uName,uPhone,uCNIC;
    String content,address,name,phone,cnic,email,id,pic;
    CircleImageView imageView;
    ImageView back;
    Uri imageUri;
    Button updateBtn;
    Bitmap photo;
    int check = 0;
    PrefManager preference;
    RetrofitClient retrofitClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        init();
        getData();
        imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pickImage();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (isValid())
                {
                    myThread();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    void setData(String email, String phone, String cnic, String name, String address, String profilepicture)
    {
        uEmail.setText(email);
        uPhone.setText(phone);
        uCNIC.setText(cnic);
        uName.setText(name);
        uAddress.setText(address);
        Picasso.get().load(RetrofitClient.BASE_URL+profilepicture).into(imageView);
        Log.d(TAG, "setData: "+imageView);

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
        back = findViewById(R.id.back);
        updateBtn = findViewById(R.id.updateBtn);
        uEmail = findViewById(R.id.email);
        uEmail.setFocusable(false);
        uAddress = findViewById(R.id.address);

        uName = findViewById(R.id.userName);
        uPhone = findViewById(R.id.userPhone);
        uCNIC = findViewById(R.id.userCNIC);
        imageView = findViewById(R.id.userImg);
    }

    void gettingStrings()
    {
        address = uAddress.getText().toString();

        name = uName.getText().toString().trim();
        phone = uPhone.getText().toString().trim();
        cnic = uCNIC.getText().toString().trim();

    }
    boolean isValid()
    {
        gettingStrings();

        if (name.isEmpty() || name.length()<3)
        {
            uName.setError("please Enter full name");
            return false;
        }
        if (address.isEmpty() || address.length() < 6) {
            uAddress.setError("address too short");
            return false;
        }

        if (phone.isEmpty()|| phone.length() >12)
        {
            uPhone.setError("Invalid Phone number");
            return false;
        }
        if (cnic.isEmpty() || cnic.length() > 14)
        {
            uCNIC.setError("Cnic number is too longer !" + "Maximum length 13 :`");
            return false;
        }
        if (check == 0)
        {
            Toast.makeText(this, "Please select a profile Pic", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    void pickImage()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
//set icon
                .setIcon(android.R.drawable.ic_menu_camera)
//set title
                .setTitle("Profile Picture")
//set message
                .setMessage("How to pick image ?")
//set positive button
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent camera_intent
                                = new Intent(MediaStore
                                .ACTION_IMAGE_CAPTURE);

                        // Start the activity with camera_intent,
                        // and request pic id
                        startActivityForResult(camera_intent, 8);    }
                })
//set negative button
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        Intent gallery = new Intent();
                        gallery.setType("image/*");
                        gallery.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),2);

                    }
                })
                .show();
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 2)
        {
            imageUri = data.getData();
            Log.d("imageUri","onActivityResult:"+imageUri);
            try {
                check= 2;
                photo = MediaStore.Images.Media.getBitmap(ProfileActivity.this.getContentResolver(),imageUri);
                imageView.setImageBitmap(photo);
                Log.d(TAG, "onActivityResult: nomi ");

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else if((requestCode == 8 && resultCode == RESULT_OK) )
        {
            check= 2;
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            Log.d(TAG, "onActivityResult: nomi ");
        }
    }
    public void updateStudent(String remail,String raddress ,String rphone,String rname,String rcnic, String rid,Uri file)
    {
        String filePath = getPathFromUri(this,file);
        File filePic = new File(filePath);
        RequestBody imagePart = RequestBody.create(MediaType.parse("*/*"), filePic);
        MultipartBody.Part profilepicture = MultipartBody.Part
                .createFormData("profilepicture",filePic.getName(),imagePart);

        RequestBody name = RequestBody.create(MultipartBody.FORM,rname);
        RequestBody email =  RequestBody.create(MultipartBody.FORM,remail);
        RequestBody address =  RequestBody.create(MultipartBody.FORM,raddress);
        RequestBody phone =   RequestBody.create(MultipartBody.FORM,rphone);
        RequestBody student_id =   RequestBody.create(MultipartBody.FORM,rid);
        RequestBody cnic =   RequestBody.create(MultipartBody.FORM,rcnic);
        RequestBody face_state =   RequestBody.create(MultipartBody.FORM,"nomi");

        Call<StudentInfo> call = RetrofitClient.getInstance().getApi()
                .updateStudent(name,address,phone,student_id,cnic,profilepicture,face_state);
        call.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {

                    if (response.body().getResult().equals("success"))
                    {

                        StudentInfo studentInfo = response.body();

                        Toast.makeText(ProfileActivity.this, "Successfully login ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: isSuccessful" + response +new Gson().toJson(studentInfo));

                        preference.setPassword(studentInfo.get_password());
                        preference.setFaceAdded("login ok");
                        Intent intent = new Intent(ProfileActivity.this, AddFace.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Log.d(TAG, "onResponse: "+response.body().getResult());
                    }
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(ProfileActivity.this, "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: noomi "+t.getMessage());

            }
        });
    }
    public void updateTeacher(String remail,String raddress ,String rphone,String rname,String rcnic, String rid,Uri file)
    {
        String filePath = getPathFromUri(this,file);
        Log.d(TAG, "uploadFile: fileUri: "+file);
        Log.d(TAG, "uploadFile: filepath: "+filePath);
        File filePic = new File(filePath);
        RequestBody imagePart = RequestBody.create(MediaType.parse("*/*"), filePic);

        MultipartBody.Part profilepicture = MultipartBody.Part
                .createFormData("profilepicture",filePic.getName(),imagePart);

        RequestBody name = RequestBody.create(MultipartBody.FORM,rname);
        RequestBody email =  RequestBody.create(MultipartBody.FORM,remail);
        RequestBody address =  RequestBody.create(MultipartBody.FORM,raddress);
        RequestBody phone =   RequestBody.create(MultipartBody.FORM,rphone);
        RequestBody teacher_id =   RequestBody.create(MultipartBody.FORM,rid);
        RequestBody cnic =   RequestBody.create(MultipartBody.FORM,rcnic);
        RequestBody face_state =   RequestBody.create(MultipartBody.FORM,"nomi");

        Call<StudentInfo> call = RetrofitClient.getInstance().getApi()
                .updateTeacher(name,address,phone,teacher_id,cnic,profilepicture,face_state);
        call.enqueue(new Callback<StudentInfo>()
        {
            @Override
            public void onResponse(Call<StudentInfo> call, Response<StudentInfo> response) {
                if (response.isSuccessful())
                {

                    if (response.body().getResult().equals("success"))
                    {

                        StudentInfo studentInfo = response.body();

                        Toast.makeText(ProfileActivity.this, "Successfully login ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onResponse: isSuccessful" + response +new Gson().toJson(studentInfo));

                        preference.setPassword(studentInfo.get_password());
                        preference.setFaceAdded("login ok");
                        Intent intent = new Intent(ProfileActivity.this, AddFace.class);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        Log.d(TAG, "onResponse: "+response.body().getResult());
                    }
                }
                else
                {
                    Toast.makeText(ProfileActivity.this, "NotSuccessful "+response, Toast.LENGTH_SHORT).show();
                    Log.d("insert", "onResponse: is NotSuccessful " + response);
                }
            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(ProfileActivity.this, "Failed "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: noomi "+t.getMessage());

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
                    email =studentInfo.getEmail();
                    phone =studentInfo.getPhone();
                    cnic =studentInfo.getCnic();
                    address = studentInfo.getAddress();
                    pic = studentInfo.getProfilepicture();
                    Log.d(TAG, "onResponse: nomi"+pic);
                    setData(email,phone,cnic,name,address,pic);
                }

            }

            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(ProfileActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+t.getMessage());
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
                    email =studentInfo.getEmail();
                    phone =studentInfo.getPhone();
                    cnic =studentInfo.getCnic();
                    address = studentInfo.getAddress();
                    pic = studentInfo.getProfilepicture();
                    Log.d(TAG, "onResponse: nomi "+pic);
                    setData(email,phone,cnic,name,address,pic);
                }
            }
            @Override
            public void onFailure(Call<StudentInfo> call, Throwable t)
            {
                Toast.makeText(ProfileActivity.this, "Error : "+t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: mohsiin "+t.getMessage());
                Log.d(TAG, "id: "+id);
            }
        });
    }
    public String getPath(Uri uri)
    {
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);
        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return  filePath;
    }
    void myThread(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                gettingStrings();
                id = preference.getUserId();
                if (content.equals("student"))
                {
                    updateStudent(email,address,phone,name,cnic,id,imageUri);

                }
                if (content.equals("teacher"))
                {
                    updateTeacher(email,address,phone,name,cnic,id,imageUri);

                }
            }
        });
        thread.start();
    }
    public static String getPathFromUri(final Context context, final Uri uri)
    {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}