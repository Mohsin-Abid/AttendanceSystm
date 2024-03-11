package com.geo.attendancesystm.model.classes.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Lectures implements Serializable
{
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("assignsubject")
    @Expose
    public String assignsubject;
    @SerializedName("assignclass")
    @Expose
    public String assignclass;
    @SerializedName("teacher")
    @Expose
    public String teacher;
    @SerializedName("starttime")
    @Expose
    public String starttime;

    @SerializedName("endtime")
    @Expose
    public String endtime;

    @SerializedName("marked")
    @Expose
    public Boolean marked;
    @SerializedName("result")
    @Expose
    public Boolean result;

    public Lectures()
    {

    }


    public String getId() {
        return id;
    }

    public String getAssignsubject() {
        return assignsubject;
    }

    public String getAssignclass() {
        return assignclass;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public Boolean getMarked() {
        return marked;
    }

    public Boolean getResult() {
        return result;
    }
}
