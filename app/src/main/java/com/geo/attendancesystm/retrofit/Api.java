package com.geo.attendancesystm.retrofit;

import com.geo.attendancesystm.model.classes.pojo.Attendance;
import com.geo.attendancesystm.model.classes.pojo.GeoFenceModel;
import com.geo.attendancesystm.model.classes.pojo.Lectures;
import com.geo.attendancesystm.model.classes.pojo.ReportModel;
import com.geo.attendancesystm.model.classes.pojo.StudentInfo;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api
{

    @FormUrlEncoded
    @POST("index.php?p=login_student")
    Call<StudentInfo> loginStudent
        (
            @Field("email") String email,
            @Field("password") String password
        );
    @FormUrlEncoded
    @POST("index.php?p=login_teacher")
    Call<StudentInfo> loginTeacher
        (
            @Field("email") String email,
            @Field("password") String password
        );

    @Multipart
    @POST("index.php?p=update_profile_students")
    Call <StudentInfo> updateStudent
            (
                    @Part("name") RequestBody name,
                    @Part("address") RequestBody address,
                    @Part("phone") RequestBody phone,
                    @Part("student_id") RequestBody id,
                    @Part("cnic") RequestBody cnic,
                    @Part MultipartBody.Part profilepicture,
                    @Part ("face_state") RequestBody face_state

            );
    @Multipart
    @POST("index.php?p=update_profile_teachers")
    Call<StudentInfo> updateTeacher
            (
                    @Part("name") RequestBody name,
                    @Part("address") RequestBody address,
                    @Part("phone") RequestBody phone,
                    @Part("teacher_id") RequestBody id,
                    @Part("cnic") RequestBody cnic,
                    @Part MultipartBody.Part profilepicture,
                    @Part ("face_state") RequestBody face_state
            );
    @FormUrlEncoded
    @POST("index.php?p=get_students")
    Call<StudentInfo> getStudent
        (
            @Field("student_id") String id
        );

    @FormUrlEncoded
    @POST("index.php?p=get_teachers")
    Call<StudentInfo> getTeacher
        (
            @Field("teacher_id") String id
        );
    @FormUrlEncoded
    @POST("index.php?p=get_classes")
    Call<StudentInfo> getClasses
        (
            @Field("id") String id
        );
    @FormUrlEncoded
    @POST("index.php?p=update_face_student")
    Call<StudentInfo> update_face_student
        (
            @Field("student_id") String student_id,
            @Field("face_state") String face_state
        );
    @FormUrlEncoded
    @POST("index.php?p=update_face_teacher")
    Call<StudentInfo> update_face_teacher
            (
                    @Field("teacher_id") String teacher_id,
                    @Field("face_state") String face_state
            );
    @FormUrlEncoded
    @POST("index.php?p=update_password_teachers")
    Call<StudentInfo> passwordTeacher
            (
                    @Field("teacher_id") String id,
                    @Field("password") String password

            );
    @FormUrlEncoded
    @POST("index.php?p=update_password_student")
    Call<StudentInfo> passwordStudent
            (
                    @Field("student_id") String id,
                    @Field("password") String password
            );

    @FormUrlEncoded
    @POST("index.php?p=check_email_teacher")
    Call<StudentInfo> checkEmailTeacher
            (
                    @Field("email") String password
            );

    @FormUrlEncoded
    @POST("index.php?p=check_email_student")
    Call<StudentInfo> checkEmailStudent
            (
                    @Field("email") String email
            );

    @POST("index.php?p=get_geofence")
    Call<GeoFenceModel> getGeoFence();

    /// reports
    @FormUrlEncoded
    @POST("index.php?p=get_reports")
    Call<ReportModel>  getAllReports(
            @Field("user_id") String user_id,
            @Field("status") String status,
            @Field("lecture_id") String lecture_id
    );


    @FormUrlEncoded
    @POST("index.php?p=get_lectures_teachers")
        Call<List<Lectures>>  get_lectures_teachers(
                @Field("id") String id
                );
    @FormUrlEncoded
    @POST("index.php?p=get_lectures_students")
    Call<List<Lectures>>  get_lectures_students(
            @Field("id") String id
    );
    @FormUrlEncoded
    @POST("index.php?p=update_attendence")
    Call<Attendance> update_attendance
            (
                    @Field("user_id") String user_id,
                    @Field("status") String status,
                    @Field("lecture_id") String lecture_id
            );

}
