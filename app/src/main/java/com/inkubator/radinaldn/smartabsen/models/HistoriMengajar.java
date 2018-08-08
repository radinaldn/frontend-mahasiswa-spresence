package com.inkubator.radinaldn.smartabsendosen.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by radinaldn on 21/07/18.
 */

public class HistoriMengajar {
    public HistoriMengajar(String idPresensi, String namaMatakuliah, String pertemuan, String namaKelas, String namaRuangan, String waktu, String totalHadir, String totalTidakHadir) {
        this.idPresensi = idPresensi;
        this.namaMatakuliah = namaMatakuliah;
        this.pertemuan = pertemuan;
        this.namaKelas = namaKelas;
        this.namaRuangan = namaRuangan;
        this.waktu = waktu;
        this.totalHadir = totalHadir;
        this.totalTidakHadir = totalTidakHadir;
    }

    @SerializedName("id_presensi")
    @Expose
    private String idPresensi;
    @SerializedName("total_hadir")
    @Expose
    private String totalHadir;
    @SerializedName("total_tidak_hadir")
    @Expose
    private String totalTidakHadir;
    @SerializedName("nama_dosen")
    @Expose
    private String namaDosen;
    @SerializedName("nama_matakuliah")
    @Expose
    private String namaMatakuliah;
    @SerializedName("pertemuan")
    @Expose
    private String pertemuan;
    @SerializedName("nama_kelas")
    @Expose
    private String namaKelas;
    @SerializedName("nama_ruangan")
    @Expose
    private String namaRuangan;
    @SerializedName("waktu")
    @Expose
    private String waktu;

    public String getIdPresensi() {
        return idPresensi;
    }

    public void setIdPresensi(String idPresensi) {
        this.idPresensi = idPresensi;
    }

    public String getNamaDosen() {
        return namaDosen;
    }

    public void setNamaDosen(String namaDosen) {
        this.namaDosen = namaDosen;
    }

    public String getNamaMatakuliah() {
        return namaMatakuliah;
    }

    public void setNamaMatakuliah(String namaMatakuliah) {
        this.namaMatakuliah = namaMatakuliah;
    }

    public String getPertemuan() {
        return pertemuan;
    }

    public void setPertemuan(String pertemuan) {
        this.pertemuan = pertemuan;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getNamaRuangan() {
        return namaRuangan;
    }

    public void setNamaRuangan(String namaRuangan) {
        this.namaRuangan = namaRuangan;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getTotalHadir() {
        return totalHadir;
    }

    public void setTotalHadir(String totalHadir) {
        this.totalHadir = totalHadir;
    }

    public String getTotalTidakHadir() {
        return totalTidakHadir;
    }

    public void setTotalTidakHadir(String totalTidakHadir) {
        this.totalTidakHadir = totalTidakHadir;
    }
}