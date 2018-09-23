package com.inkubator.radinaldn.smartabsen.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.activities.HistoriMengajarActivity;
import com.inkubator.radinaldn.smartabsen.models.Mengambil;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;

import java.util.ArrayList;

/**
 * Created by radinaldn on 24/07/18.
 */

public class MengambilAdapter extends RecyclerView.Adapter<MengambilAdapter.MengambilViewHolder> {

    private Context mContext;

    private ArrayList<Mengambil> dataList;
    private static final String TAG = MengambilAdapter.class.getSimpleName();

    ApiInterface apiService;

    public MengambilAdapter(ArrayList<Mengambil> dataList, Context context) {
        this.dataList = dataList;
        this.mContext = context;
    }

    private static final String TAG_ID_MENGAJAR = "id_mengajar";
    private static final String TAG_NAMA_DOSEN = "nama_dosen";

    @Override
    public MengambilViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.mengambil_item, parent, false);
        apiService = ApiClient.getClient().create(ApiInterface.class);

        return new MengambilViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MengambilViewHolder holder, int position) {
        holder.tv_id_mengajar.setText(dataList.get(position).getIdMengajar());
        holder.tv_matakuliah.setText(dataList.get(position).getNamaMatakuliah());
        holder.tv_nama_dosen.setText(dataList.get(position).getNamaDosen());
        holder.tv_kelas.setText("Kelas "+dataList.get(position).getNama_kelas());
        holder.tv_sks.setText(dataList.get(position).getSks()+" SKS");
        holder.tv_waktu.setText("Pukul "+dataList.get(position).getWaktuMulai());

        if (Integer.parseInt(dataList.get(position).getSisaJatah())<0){
            holder.tv_sisa_jatah.setText("TIDAK DAPAT MENGIKUTI UAS");
            holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textDangerDark));
        } else {
            holder.tv_sisa_jatah.setText(" Sisa jatah : "+dataList.get(position).getSisaJatah());

            // beri warna pada text sisa jatah
            switch (dataList.get(position).getSisaJatah()){
                case "4":
                    holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textSuccessDark));
                    break;
                case "3":
                    holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textSuccess));
                    break;
                case "2":
                    holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textWarning));
                    break;
                case "1":
                    holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textDanger));
                    break;
                case "0":
                    holder.tv_sisa_jatah.setTextColor(mContext.getResources().getColor(R.color.textDangerDark));
                    break;
        }

        }

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class MengambilViewHolder extends RecyclerView.ViewHolder {

        private static final String TAG_ID_PRESENSI = "id_presensi";
        private TextView tv_id_mengajar, tv_matakuliah, tv_nama_dosen, tv_kelas, tv_sks, tv_waktu, tv_sisa_jatah;
        private Button bt_detail;

        public MengambilViewHolder(final View itemView) {
            super(itemView);
            tv_id_mengajar = itemView.findViewById(R.id.tv_idmengajar);
            tv_matakuliah = itemView.findViewById(R.id.tv_matakuliah);
            tv_nama_dosen = itemView.findViewById(R.id.tv_nama_dosen);
            tv_kelas = itemView.findViewById(R.id.tv_kelas);
            tv_sks = itemView.findViewById(R.id.tv_sks);
            tv_waktu = itemView.findViewById(R.id.tv_waktu);
            tv_sisa_jatah = itemView.findViewById(R.id.tv_sisa_jatah);

            bt_detail = itemView.findViewById(R.id.bt_detail);

            bt_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    // redirect to HistoriMengajarActitivty
//                    Intent intent = new Intent(itemView.getContext(), HistoriMengajarActivity.class);
//                    intent.putExtra(TAG_ID_MENGAJAR, tv_id_mengajar.getText());
//                    intent.putExtra(TAG_NAMA_DOSEN, tv_nama_dosen.getText());
//                    itemView.getContext().startActivity(intent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // redirect to HistoriMengajarActitivty
                    Intent intent = new Intent(itemView.getContext(), HistoriMengajarActivity.class);
                    intent.putExtra(TAG_ID_MENGAJAR, tv_id_mengajar.getText());
                    intent.putExtra(TAG_NAMA_DOSEN, tv_nama_dosen.getText());
                    itemView.getContext().startActivity(intent);
                }
            });
        }


    }
}
