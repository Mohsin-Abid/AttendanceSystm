package com.geo.attendancesystm.model.classes.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReportModel implements Serializable {
    @SerializedName("total")
    @Expose
    public int total;
    @SerializedName("present")
    @Expose
    public int present;
    @SerializedName("absent")
    @Expose
    public int absent;

    public int getTotal() {
        return total;
    }

    public int getPresent() {
        return present;
    }

    public int getAbsent() {
        return absent;
    }
}
