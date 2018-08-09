package com.inkubator.radinaldn.smartabsen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by radinaldn on 25/07/18.
 */

public class PresensiDetail {

    public PresensiDetail(String idPresensi, String nim, String namaMahasiswa, String status, String lat, String lng, String waktu, String jarak, String proses, String foto_mahasiswa) {
        this.idPresensi = idPresensi;
        this.nim = nim;
        this.namaMahasiswa = namaMahasiswa;
        this.status = status;
        this.lat = lat;
        this.lng = lng;
        this.waktu = waktu;
        this.jarak = jarak;
        this.proses = proses;
        this.foto_mahasiswa = foto_mahasiswa;
    }

    @SerializedName("id_presensi")
    @Expose
    private String idPresensi;

    @SerializedName("nim")
    @Expose
    private String nim;

    @SerializedName("nama_mahasiswa")
    @Expose
    private String namaMahasiswa;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("waktu")
    @Expose
    private String waktu;

    @SerializedName("jarak")
    @Expose
    private String jarak;

    @SerializedName("foto_mahasiswa")
    @Expose
    private String foto_mahasiswa;

    @SerializedName("proses")
    @Expose
    private String proses;

    public String getFoto_mahasiswa() {
        return foto_mahasiswa;
    }

    public void setFoto_mahasiswa(String foto_mahasiswa) {
        this.foto_mahasiswa = foto_mahasiswa;
    }

    public String getIdPresensi() {

        return idPresensi;
    }

    public void setIdPresensi(String idPresensi) {
        this.idPresensi = idPresensi;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNamaMahasiswa() {
        return namaMahasiswa;
    }

    public void setNamaMahasiswa(String namaMahasiswa) {
        this.namaMahasiswa = namaMahasiswa;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getJarak() {
        return jarak;
    }

    public void setJarak(String jarak) {
        this.jarak = jarak;
    }

    public String getProses() {
        return proses;
    }

    public void setProses(String proses) {
        this.proses = proses;
    }
}
