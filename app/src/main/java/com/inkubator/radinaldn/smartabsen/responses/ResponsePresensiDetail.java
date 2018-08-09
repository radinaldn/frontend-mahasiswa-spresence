package com.inkubator.radinaldn.smartabsen.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.PresensiDetail;

import java.util.List;

/**
 * Created by radinaldn on 25/07/18.
 */

public class ResponsePresensiDetail {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("master")
    @Expose
    private List<PresensiDetail> presensiDetail = null;

    public ResponsePresensiDetail(List<PresensiDetail> presensiDetail){
        this.presensiDetail= presensiDetail;
    }

    public List<PresensiDetail> getPresensiDetail() {
        return this.presensiDetail;
    }
}
