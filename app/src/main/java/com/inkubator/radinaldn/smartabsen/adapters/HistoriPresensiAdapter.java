package com.inkubator.radinaldn.smartabsen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.models.PresensiDetail;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by radinaldn on 25/07/18.
 */

public class HistoriPresensiAdapter extends RecyclerView.Adapter<HistoriPresensiAdapter.HistoriPresensiViewHolder> {

    private Context mContext;
    SessionManager sessionManager;

    private ArrayList<PresensiDetail> dataList;
    private static final String TAG = HistoriPresensiAdapter.class.getSimpleName();
    private static final String TAG_NIM = "nim";

    ApiInterface apiService;

    public String ID_PRESENSI;

    public HistoriPresensiAdapter(ArrayList<PresensiDetail> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HistoriPresensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.histori_presensi_item, parent, false);

        sessionManager = new SessionManager(mContext);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        return new HistoriPresensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriPresensiViewHolder holder, int position) {

        // pemberian warna pada cardview utk data diri sendiri
        System.out.println("dataList.get(position).getNim() : "+dataList.get(position).getNim());
        System.out.println("sessionManager.getMahasiswaDetail.get(TAG_NIM) : "+sessionManager.getMahasiswaDetail().get(TAG_NIM));
        if (dataList.get(position).getNim().equalsIgnoreCase(sessionManager.getMahasiswaDetail().get(TAG_NIM))){
            System.out.println(dataList.get(position).getNim()+" == "+sessionManager.getMahasiswaDetail().get(TAG_NIM));
            if (dataList.get(position).getStatus().equalsIgnoreCase(PresensiDetail.HADIR)){
                holder.cv_histori_presensi.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorMint));
            } else {
                holder.cv_histori_presensi.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorBitterSweet));
            }
        }

        holder.tv_id_presensi.setText(dataList.get(position).getIdPresensi());
        ID_PRESENSI = dataList.get(position).getIdPresensi();
        holder.tv_nim.setText(dataList.get(position).getNim());
        holder.tv_nama_mahasiswa.setText(dataList.get(position).getNamaMahasiswa());
        holder.tv_waktu.setText(dataList.get(position).getWaktu());
        holder.tv_jarak.setText(dataList.get(position).getJarak()+" Meter");
        holder.tv_status.setText(dataList.get(position).getStatus());

        int jarak = Integer.parseInt(dataList.get(position).getJarak());
        if (jarak >0 && jarak <= 50){
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.GreenBootstrap));
        } else if (jarak > 50 && jarak <100) {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.orange));
        } else if (jarak > 100){
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.RedBootstrap));
        } else {
            holder.tv_jarak.setTextColor(mContext.getResources().getColor(R.color.colorBlueJeansDark));
        }

        switch (dataList.get(position).getStatus()){
            case "Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.GreenBootstrap));
                break;
            case "Tidak Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.RedBootstrap));
                break;

        }

        holder.tv_proses.setText(dataList.get(position).getProses());
        Picasso.with(holder.itemView.getContext()).load(ServerConfig.IMAGE_PATH+"mahasiswa/"+dataList.get(position).getFoto_mahasiswa())
                .fit()
                .placeholder(R.drawable.dummy_ava)
                .error(R.drawable.dummy_ava)
                .centerCrop()
                .fit()
                .into(holder.iv_avatar);
        if(dataList.get(position).getProses().equalsIgnoreCase("Pending")){
            holder.iv_proses.setImageResource(R.drawable.ic_on_proggres);
        } else {
            holder.iv_proses.setImageResource(R.drawable.ic_clear_green);
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class HistoriPresensiViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_histori_presensi;
        private TextView tv_id_presensi, tv_nim, tv_nama_mahasiswa, tv_status, tv_lat, tv_lng, tv_waktu, tv_jarak, tv_proses;
        private ImageView iv_avatar, iv_proses;

        public HistoriPresensiViewHolder(View itemView) {
            super(itemView);
            cv_histori_presensi = itemView.findViewById(R.id.cv_histori_presensi_item);
            tv_id_presensi = itemView.findViewById(R.id.tv_id_presensi);
            tv_nim = itemView.findViewById(R.id.tv_nim);
            tv_nama_mahasiswa = itemView.findViewById(R.id.tv_nama);
            tv_waktu = itemView.findViewById(R.id.tv_waktu);
            tv_jarak = itemView.findViewById(R.id.tv_jarak);
            tv_status = itemView.findViewById(R.id.tv_status);
            tv_proses = itemView.findViewById(R.id.tv_proses);
            iv_avatar = itemView.findViewById(R.id.iv_foto);
            iv_proses = itemView.findViewById(R.id.iv_proses);

        }
    }
}
