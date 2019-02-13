package com.inkubator.radinaldn.smartabsen.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.models.KehadiranDosen;
import com.inkubator.radinaldn.smartabsen.rests.ApiClient;
import com.inkubator.radinaldn.smartabsen.rests.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by radinaldn on 05/08/18.
 */

public class KehadiranDosenAdapter extends RecyclerView.Adapter<KehadiranDosenAdapter.KehadiranDosenViewHolder> {

    private Context context;

    private ArrayList<KehadiranDosen> dataList;
    private static final String TAG = KehadiranDosenAdapter.class.getSimpleName();

    ApiInterface apiService;

    public KehadiranDosenAdapter(Context context, ArrayList<KehadiranDosen> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public KehadiranDosenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        final View view = layoutInflater.inflate(R.layout.kehadiran_dosen_item, parent, false);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        return new KehadiranDosenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KehadiranDosenViewHolder holder, int position) {
        Picasso.with(context)
                .load(ServerConfig.IMAGE_PATH + "dosen/" + dataList.get(position).getFoto())
                .fit()
                .placeholder(R.drawable.dummy_ava)
                .error(R.drawable.dummy_ava)
                .centerCrop()
                .fit()
                .into(holder.iv_foto);

        holder.tv_nip.setText(dataList.get(position).getNip());

        holder.tv_nama_dosen.setText(dataList.get(position).getNama_dosen());
        holder.tv_status_kehadiran.setText(dataList.get(position).getStatus_kehadiran());
        switch (dataList.get(position).getStatus_kehadiran()) {
            case "Hadir":
                holder.tv_status_kehadiran.setBackgroundColor(context.getResources().getColor(R.color.GreenBootstrap));
                break;
            case "Tidak Hadir":
                holder.tv_status_kehadiran.setBackgroundColor(context.getResources().getColor(R.color.RedBootstrap));
                break;

        }

        // cek apakah nama_kota == null
        if (dataList.get(position).getNama_kota() != null && !dataList.get(position).getNama_kota().equalsIgnoreCase("")) {
            holder.tv_nama_kota.setText(dataList.get(position).getNama_kota());
            holder.tv_nama_kota.setVisibility(View.VISIBLE);
        }

        holder.tv_last_update.setText(dataList.get(position).getLast_update());
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class KehadiranDosenViewHolder extends RecyclerView.ViewHolder {

        TextView tv_nip, tv_nama_dosen, tv_status_kehadiran, tv_nama_kota, tv_last_update;
        ImageView iv_foto;

        public KehadiranDosenViewHolder(View itemView) {
            super(itemView);
            tv_nip = itemView.findViewById(R.id.tv_nip);
            tv_nama_dosen = itemView.findViewById(R.id.tv_nama_dosen);
            tv_status_kehadiran = itemView.findViewById(R.id.tv_status_kehadiran);
            tv_nama_kota = itemView.findViewById(R.id.tv_nama_kota);
            tv_last_update = itemView.findViewById(R.id.tv_last_update);
            iv_foto = itemView.findViewById(R.id.iv_foto);

        }
    }
}
