package com.inkubator.radinaldn.smartabsen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by radinaldn on 05/08/18.
 */

public class KehadiranDosen {
    @SerializedName("nip")
    @Expose
    private String nip;
    @SerializedName("nama_dosen")
    @Expose
    private String nama_dosen;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("status_kehadiran")
    @Expose
    private String status_kehadiran;
    @SerializedName("nama_kota")
    @Expose
    private String nama_kota;
    @SerializedName("last_update")
    @Expose
    private String last_update;

    public KehadiranDosen(String nip, String nama_dosen, String foto, String status_kehadiran, String nama_kota, String last_update) {
        this.nip = nip;
        this.nama_dosen = nama_dosen;
        this.foto = foto;
        this.status_kehadiran = status_kehadiran;
        this.nama_kota = nama_kota;
        this.last_update = last_update;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNama_dosen() {
        return nama_dosen;
    }

    public void setNama_dosen(String nama_dosen) {
        this.nama_dosen = nama_dosen;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus_kehadiran() {
        return status_kehadiran;
    }

    public void setStatus_kehadiran(String status_kehadiran) {
        this.status_kehadiran = status_kehadiran;
    }

    public String getNama_kota() {
        return nama_kota;
    }

    public void setNama_kota(String nama_kota) {
        this.nama_kota = nama_kota;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }
}
