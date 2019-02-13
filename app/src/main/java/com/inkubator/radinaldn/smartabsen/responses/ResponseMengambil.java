package com.inkubator.radinaldn.smartabsen.responses;

/**
 * Created by radinaldn on 24/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;

import java.util.List;

public class ResponseMengambil {

    @SerializedName("master")
    @Expose
    private List<Mengambil> mengambil = null;

    public ResponseMengambil(List<Mengambil> mengambil) {
        this.mengambil = mengambil;
    }

    public List<Mengambil> getMengambil() {
        return mengambil;
    }

    public void setMengambil(List<Mengambil> mengambil) {
        this.mengambil = mengambil;
    }

}