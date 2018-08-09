package com.inkubator.radinaldn.smartabsen.responses;

/**
 * Created by radinaldn on 03/07/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.Mahasiswa;



import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseLogin {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private List<Mahasiswa> data = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Mahasiswa> getData() {
        return data;
    }

    public void setData(List<Mahasiswa> data) {
        this.data = data;
    }

}