package com.inkubator.radinaldn.smartabsen.responses;

/**
 * Created by radinaldn on 11/08/18.
 */

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.HistoriPerkuliahan;

public class ResponseHistoriPerkuliahan {

    @SerializedName("master")
    @Expose
    private List<HistoriPerkuliahan> master = null;

    public List<HistoriPerkuliahan> getMaster() {
        return master;
    }

    public void setMaster(List<HistoriPerkuliahan> master) {
        this.master = master;
    }

}