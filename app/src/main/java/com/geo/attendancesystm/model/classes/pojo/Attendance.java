package com.geo.attendancesystm.model.classes.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Attendance implements Serializable {
    @SerializedName("user_id")
    @Expose
    public String user_id;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("lecture_id")
    @Expose
    public String lecture_id;
    @SerializedName("result")
    @Expose
    public String result;

    public String getUser_id() {
        return user_id;
    }

    public String getStatus() {
        return status;
    }

    public String getLecture_id() {
        return lecture_id;
    }

    public String getResult() {
        return result;
    }
}
