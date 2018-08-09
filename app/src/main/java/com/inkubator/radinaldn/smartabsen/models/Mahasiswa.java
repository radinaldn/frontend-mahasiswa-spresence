package com.inkubator.radinaldn.smartabsen.models;

/**
 * Created by radinaldn on 03/07/18.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Mahasiswa {

    @SerializedName("nim")
    @Expose
    private String nim;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("id_telegram")
    @Expose
    private String idTelegram;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("jk")
    @Expose
    private String jk;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("id_jurusan")
    @Expose
    private String idJurusan;

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getIdTelegram() {
        return idTelegram;
    }

    public void setIdTelegram(String idTelegram) {
        this.idTelegram = idTelegram;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJk() {
        return jk;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdJurusan() {
        return idJurusan;
    }

    public void setIdJurusan(String idJurusan) {
        this.idJurusan = idJurusan;
    }

}
