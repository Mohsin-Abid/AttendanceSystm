package com.geo.attendancesystm.model.classes.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StudentInfo{

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("teacher_id")
    @Expose
    public String teacher_id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("phone")
    @Expose
    public String phone;
    @SerializedName("cnic")
    @Expose
    public String cnic;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("dob")
    @Expose
    public String dob;
    @SerializedName("class")
    @Expose
    public String _class;
    @SerializedName("password")
    @Expose
    public String _password;
    @SerializedName("result")
    @Expose
    public String result;

    @SerializedName("face_state")
    @Expose
    public String face_state;
    @SerializedName("profilepicture")
    @Expose
    public String profilepicture;
    public StudentInfo()
    {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String get_class() {
        return _class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String get_password() {
        return _password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFace_state() {
        return face_state;
    }

    public void setFace_state(String face_state) {
        this.face_state = face_state;
    }

    public String getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(String teacher_id) {
        this.teacher_id = teacher_id;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}