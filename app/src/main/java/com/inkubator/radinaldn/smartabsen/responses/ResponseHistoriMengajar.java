package com.inkubator.radinaldn.smartabsen.responses;

/**
 * Created by radinaldn on 21/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.inkubator.radinaldn.smartabsen.models.HistoriMengajar;

import java.util.List;

public class ResponseHistoriMengajar {

    @SerializedName("master")
    @Expose
    private List<HistoriMengajar> historiMengajar = null;

    public List<HistoriMengajar> getHistoriMengajar() {
        return historiMengajar;
    }

    public void setHistoriMengajar(List<HistoriMengajar> historiMengajarr) {
        this.historiMengajar = historiMengajarr;
    }

}


