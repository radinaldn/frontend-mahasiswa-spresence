package com.inkubator.radinaldn.smartabsen.rests;

import com.inkubator.radinaldn.smartabsen.responses.ResponseHistoriMengajar;
import com.inkubator.radinaldn.smartabsen.responses.ResponseHistoriPerkuliahan;
import com.inkubator.radinaldn.smartabsen.responses.ResponseIsiPresensi;
import com.inkubator.radinaldn.smartabsen.responses.ResponseKehadiranDosen;
import com.inkubator.radinaldn.smartabsen.responses.ResponseLogin;
import com.inkubator.radinaldn.smartabsen.responses.ResponseMengambil;
import com.inkubator.radinaldn.smartabsen.responses.ResponsePresensiDetail;
import com.inkubator.radinaldn.smartabsen.responses.ResponseUpdateImei;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by radinaldn on 03/07/18.
 */

public interface ApiInterface {

    /*
    API Mahasiswa
     */

    // untuk mendapatkan data kehadiran dosen berdasarkan inputan nama
    @GET("dosen/kehadiran-dosen-find-by-name")
    Call<ResponseKehadiranDosen> dosenKehadiranFindByName(
            @Query("name") String name
    );

    // untuk mendapatkan data histori perkuliah by nim and date
    @GET("presensi-detail/find-by-nim-and-date")
    Call<ResponseHistoriPerkuliahan> presensiDetailFindByNimAndDate(
            @Query("nim") String nim,
            @Query("date") String date
    );

    // untuk mendapatkan data kuliah hari ini
    @GET("mengambil/find-all-today-by-nim")
    Call<ResponseMengambil> mengambilFindAllTodayByNim(
            @Query("nim") String nim
    );

    // untuk mendapatkan data kehadiran dosen
    @GET("dosen/find-all-by-status-kehadiran")
    Call<ResponseKehadiranDosen> dosenFindAllByStatusKehadiran(
            @Query("status_kehadiran") String status_kehadiran
    );

    // untuk mendapatkan data mahasiswa yang berhasil scan qr-code
    @GET("presensi-detail/find-all-mahasiswa-by-id-presensi-and-status-kehadiran")
    Call<ResponsePresensiDetail> presensiDetailFindAllMahasiswaByIdPresensiAndStatusKehadiran(
            @Query("id_presensi") String id_presensi,
            @Query("status_kehadiran") String status_kehadiran
    );

    // untuk mendapatkan histori mengajar (day 1, 2, ...) by id_presensi
    @GET("presensi/histori-mengajar-by-id-mengajar")
    Call<ResponseHistoriMengajar> presensiHistoriMengajarByIdMengajar(
            @Query("id_mengajar") String id_mengajar
    );

    // untuk mendapatkan jadwal mengajar mahasiswa berdasarkan inputan nim dan dayname
    @GET("mengambil/find-all-by-nim-and-dayname")
    Call<ResponseMengambil> mengambilFindAllByNimAndDayname(
            @Query("nim") String nim,
            @Query("dayname") String dayname
    );

    // untuk login
    @FormUrlEncoded
    @POST("mahasiswa/login")
    Call<ResponseLogin> login(
            @Field("username") String nim,
            @Field("password") String password,
            @Field("imei") String imei
    );

    @FormUrlEncoded
    @POST("mahasiswa/login-nim-and-password")
    Call<ResponseLogin> loginNimAndPassword(
            @Field("username") String nim,
            @Field("password") String password
    );

    // untuk mengirim presensi
    @FormUrlEncoded
    @POST("presensi-detail/isi-presensi")
    Call<ResponseIsiPresensi> isiPresensi(
            @Field("id_presensi") String id_presensi,
            @Field("nim") String nim,
            @Field("lat") String lat,
            @Field("lng") String lng,
            @Field("jarak") String jarak
    );

    @FormUrlEncoded
    @POST("mahasiswa/update-imei")
    Call<ResponseUpdateImei> mahasiswaUpdateImei(
            @Field("nim") String nim,
            @Field("imei") String imei
    );

    @FormUrlEncoded
    @POST("mahasiswa/update-id-telegram")
    Call<ResponseUpdateImei> mahasiswaUpdateIdTelegram(
            @Field("nim") String nim,
            @Field("id_telegram") String id_telegram
    );

    @FormUrlEncoded
    @POST("mahasiswa/update-foto")
    Call<ResponseUpdateImei> mahasiswaUpdateFoto(
            @Field("nim") String nim,
            @Field("foto") String foto
    );


}
