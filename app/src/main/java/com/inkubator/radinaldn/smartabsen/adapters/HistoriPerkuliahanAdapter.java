package com.inkubator.radinaldn.smartabsen.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.activities.HistoriPresensiActivity;
import com.inkubator.radinaldn.smartabsen.models.HistoriPerkuliahan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by radinaldn on 11/08/18.
 */

public class HistoriPerkuliahanAdapter extends RecyclerView.Adapter<HistoriPerkuliahanAdapter.HistoriPerkuliahanViewHolder> {

    private List<HistoriPerkuliahan> dataList;
    private static final String TAG = HistoriPresensiAdapter.class.getSimpleName();
    private static final String TAG_ID_PRESENSI = "id_presensi";
    private static final String TAG_NAMA_MATAKULIAH = "nama_matakuliah";
    private static final String TAG_PERTEMUAN = "pertemuan";
    public String ID_PRESENSI;

    Context mContext;

    public HistoriPerkuliahanAdapter(List<HistoriPerkuliahan> dataList, Context context) {

        this.dataList = dataList;
        this.mContext = context;


    }

    @NonNull
    @Override
    public HistoriPerkuliahanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.histori_perkuliahan_item, parent, false);

        return new HistoriPerkuliahanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriPerkuliahanViewHolder holder, int position) {
        holder.tv_id_presensi.setText(dataList.get(position).getIdPresensi());
        holder.tv_matakuliah.setText(dataList.get(position).getNamaMatakuliah());
        holder.tv_nama_dosen.setText(dataList.get(position).getNamaDosen());
        holder.tv_pertemuan.setText("Pertemuan "+dataList.get(position).getPertemuan());
        holder.tv_kelas.setText("Kelas "+dataList.get(position).getNamaKelas());
        holder.tv_ruangan.setText(dataList.get(position).getNamaRuangan());
        holder.tv_waktu.setText(dataList.get(position).getWaktu());
        holder.tv_status.setText(dataList.get(position).getStatus());

        switch (dataList.get(position).getStatus()){
            case "Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.GreenBootstrap));
                break;
            case "Tidak Hadir":
                holder.tv_status.setBackgroundColor(mContext.getResources().getColor(R.color.RedBootstrap));
                break;

        }

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class HistoriPerkuliahanViewHolder extends RecyclerView.ViewHolder {

        TextView tv_id_presensi, tv_matakuliah, tv_nama_dosen, tv_pertemuan, tv_kelas, tv_ruangan, tv_waktu, tv_status;
        Button bt_detail;

        public HistoriPerkuliahanViewHolder(final View itemView) {
            super(itemView);
            tv_id_presensi = itemView.findViewById(R.id.tv_id_presensi);
            tv_matakuliah = itemView.findViewById(R.id.tv_matakuliah);
            tv_nama_dosen = itemView.findViewById(R.id.tv_nama_dosen);
            tv_pertemuan = itemView.findViewById(R.id.tv_pertemuan);
            tv_kelas = itemView.findViewById(R.id.tv_kelas);
            tv_ruangan = itemView.findViewById(R.id.tv_ruangan);
            tv_waktu = itemView.findViewById(R.id.tv_waktu);
            tv_status = itemView.findViewById(R.id.tv_status);
            bt_detail = itemView.findViewById(R.id.bt_detail);

            bt_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), HistoriPresensiActivity.class);
                    Log.d(TAG, "onClick: tv_id_presensi : "+tv_id_presensi.getText());
                    i.putExtra(TAG_ID_PRESENSI, tv_id_presensi.getText());
                    i.putExtra(TAG_NAMA_MATAKULIAH, tv_matakuliah.getText());
                    i.putExtra(TAG_PERTEMUAN, tv_pertemuan.getText());
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }
}
