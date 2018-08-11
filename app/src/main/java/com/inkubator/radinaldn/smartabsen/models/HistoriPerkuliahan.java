package com.inkubator.radinaldn.smartabsen.models;

/**
 * Created by radinaldn on 11/08/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/*
model ini digunakan untuk menampilkan data riwayat perkuliahan per hari yang sudah lalu
 */
public class HistoriPerkuliahan {

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
    @SerializedName("waktu")
    @Expose
    private String waktu;
    @SerializedName("nama_matakuliah")
    @Expose
    private String namaMatakuliah;
    @SerializedName("nama_dosen")
    @Expose
    private String namaDosen;
    @SerializedName("nama_kelas")
    @Expose
    private String namaKelas;
    @SerializedName("pertemuan")
    @Expose
    private String pertemuan;
    @SerializedName("nama_ruangan")
    @Expose
    private String namaRuangan;

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

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
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

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getPertemuan() {
        return pertemuan;
    }

    public void setPertemuan(String pertemuan) {
        this.pertemuan = pertemuan;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }

    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

}