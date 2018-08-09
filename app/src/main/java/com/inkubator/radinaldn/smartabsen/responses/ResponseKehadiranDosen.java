package com.inkubator.radinaldn.smartabsen.responses;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.KehadiranDosen;

import java.util.List;

/**
 * Created by radinaldn on 05/08/18.
 */

public class ResponseKehadiranDosen {
    @SerializedName("master")
    @Expose
    private List<KehadiranDosen> kehadiranDosen = null;

    public ResponseKehadiranDosen(List<KehadiranDosen> kehadiranDosen) {
        this.kehadiranDosen = kehadiranDosen;
    }

    public List<KehadiranDosen> getKehadiranDosen() {
        return kehadiranDosen;
    }
}
