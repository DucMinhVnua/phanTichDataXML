package com.example.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readxml.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class adapterXML extends RecyclerView.Adapter<adapterXML.mViewHolder>{

    Context mcontext;
    List<obj> mList;

    public adapterXML(Context mcontext, List<obj> mList) {
        this.mcontext = mcontext;
        this.mList = mList;
    }

    @NonNull
    @NotNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_itemfilexml, parent, false);
        return new mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull mViewHolder holder, int position) {
        obj mobj = mList.get(position);
        if (mobj == null) {
            return;
        }
        holder.tvUrl.setText("URL: " + mobj.getUrl());
        holder.tvI.setText("Images: " + String.valueOf(mobj.getI()));
        holder.tvPhanTram.setText("Priority: " + String.valueOf(mobj.getPhanTram()));
        holder.tvTen.setText("Change Frequency: " + mobj.getTen());
        holder.tvNgayThang.setText("Last Change: " + mobj.getNgayThang());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        TextView tvUrl, tvI, tvPhanTram, tvTen, tvNgayThang;
        public mViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvUrl = itemView.findViewById(R.id.tv_url);
            tvI = itemView.findViewById(R.id.tv_i);
            tvPhanTram = itemView.findViewById(R.id.tv_phanTram);
            tvTen = itemView.findViewById(R.id.tv_ten);
            tvNgayThang = itemView.findViewById(R.id.tv_ngayThang);
        }
    }
}
