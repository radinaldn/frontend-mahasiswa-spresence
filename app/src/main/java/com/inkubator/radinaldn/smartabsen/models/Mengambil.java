package com.inkubator.radinaldn.smartabsen.models;

/**
 * Created by radinaldn on 24/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mengambil {

    @SerializedName("id_mengambil")
    @Expose
    private String idMengambil;
    @SerializedName("id_mengajar")
    @Expose
    private String idMengajar;
    @SerializedName("sisa_jatah")
    @Expose
    private String sisaJatah;
    @SerializedName("nama_matakuliah")
    @Expose
    private String namaMatakuliah;
    @SerializedName("nama_dosen")
    @Expose
    private String namaDosen;
    @SerializedName("waktu_mulai")
    @Expose
    private String waktuMulai;
    @SerializedName("hari")
    @Expose
    private String hari;
    @SerializedName("sks")
    @Expose
    private String sks;
    @SerializedName("nama_kelas")
    @Expose
    private String nama_kelas;

    public Mengambil(String idMengambil, String idMengajar, String sisaJatah, String namaMatakuliah, String namaDosen, String waktuMulai, String hari, String sks, String nama_kelas) {
        this.idMengambil = idMengambil;
        this.idMengajar = idMengajar;
        this.sisaJatah = sisaJatah;
        this.namaMatakuliah = namaMatakuliah;
        this.namaDosen = namaDosen;
        this.waktuMulai = waktuMulai;
        this.hari = hari;
        this.sks = sks;
        this.nama_kelas = nama_kelas;
    }

    public String getIdMengambil() {
        return idMengambil;
    }

    public void setIdMengambil(String idMengambil) {
        this.idMengambil = idMengambil;
    }

    public String getIdMengajar() {
        return idMengajar;
    }

    public void setIdMengajar(String idMengajar) {
        this.idMengajar = idMengajar;
    }

    public String getSisaJatah() {
        return sisaJatah;
    }

    public void setSisaJatah(String sisaJatah) {
        this.sisaJatah = sisaJatah;
    }

    public String getNamaMatakuliah() {
        return namaMatakuliah;
    }

    public void setNamaMatakuliah(String namaMatakuliah) {
        this.namaMatakuliah = namaMatakuliah;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getWaktuMulai() {
        return waktuMulai;
    }

    public void setWaktuMulai(String waktuMulai) {
        this.waktuMulai = waktuMulai;
    }

    public String getHari() {
        return hari;
    }

    public void setHari(String hari) {
        this.hari = hari;
    }

    public String getSks() {
        return sks;
    }

    public void setSks(String sks) {
        this.sks = sks;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }

    public void setNama_kelas(String nama_kelas) {
        this.nama_kelas = nama_kelas;
    }
}

