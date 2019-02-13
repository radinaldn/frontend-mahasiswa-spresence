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
import com.inkubator.radinaldn.smartabsen.models.HistoriMengajar;

import java.util.ArrayList;

/**
 * Created by radinaldn on 25/07/18.
 */

public class HistoriMengajarAdapter extends RecyclerView.Adapter<HistoriMengajarAdapter.HistoriPresensiViewHolder> {

    private ArrayList<HistoriMengajar> dataList;
    private static final String TAG = HistoriMengajarAdapter.class.getSimpleName();
    private static final String TAG_ID_PRESENSI = "id_presensi";
    private static final String TAG_NAMA_MATAKULIAH = "nama_matakuliah";
    private static final String TAG_PERTEMUAN = "pertemuan";
    private static final String TAG_STATUS_PRESENSI = "status_presensi";

    public String ID_PRESENSI;
    public final String STATUS_PRESENSI = "close";
    private Context mContext;

    public HistoriMengajarAdapter(ArrayList<HistoriMengajar> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public HistoriPresensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.histori_mengajar_item, parent, false);

        return new HistoriPresensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriPresensiViewHolder holder, int position) {
        holder.tv_id_presensi.setText(dataList.get(position).getIdPresensi());
        ID_PRESENSI = dataList.get(position).getIdPresensi();
        holder.tv_matakuliah.setText(dataList.get(position).getNamaMatakuliah());
        holder.tv_pertemuan.setText(mContext.getResources().getString(R.string.pertemuan) + " " + dataList.get(position).getPertemuan());
        holder.tv_kelas.setText(mContext.getResources().getString(R.string.kelas) + " " + dataList.get(position).getNamaKelas());
        holder.tv_ruangan.setText(mContext.getResources().getString(R.string.ruangan) + " " + dataList.get(position).getNamaRuangan());
        holder.tv_waktu.setText(dataList.get(position).getWaktu());
        holder.tv_jlhadir.setText(" " + dataList.get(position).getTotalHadir());
        holder.tv_jltdkhadir.setText("  " + dataList.get(position).getTotalTidakHadir());

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class HistoriPresensiViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_id_presensi, tv_matakuliah, tv_pertemuan, tv_kelas, tv_ruangan, tv_waktu, tv_jlhadir, tv_jltdkhadir;
        private Button bt_detail;

        public HistoriPresensiViewHolder(final View itemView) {
            super(itemView);
            tv_id_presensi = itemView.findViewById(R.id.tv_id_presensi);
            tv_matakuliah = itemView.findViewById(R.id.tv_matakuliah);
            tv_pertemuan = itemView.findViewById(R.id.tv_pertemuan);
            tv_kelas = itemView.findViewById(R.id.tv_kelas);
            tv_ruangan = itemView.findViewById(R.id.tv_ruangan);
            tv_waktu = itemView.findViewById(R.id.tv_waktu);
            bt_detail = itemView.findViewById(R.id.bt_detail);
            tv_jlhadir = itemView.findViewById(R.id.tv_jlhadir);
            tv_jltdkhadir = itemView.findViewById(R.id.tv_jltdkhadir);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // action click histori mengajar pertemuan sekian

                }
            });

            bt_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(itemView.getContext(), HistoriPresensiActivity.class);
                    Log.d(TAG, "onClick: tv_id_presensi : " + tv_id_presensi.getText());
                    i.putExtra(TAG_ID_PRESENSI, tv_id_presensi.getText());
                    i.putExtra(TAG_NAMA_MATAKULIAH, tv_matakuliah.getText());
                    i.putExtra(TAG_PERTEMUAN, tv_pertemuan.getText());
                    itemView.getContext().startActivity(i);
                }
            });


        }


    }
}
