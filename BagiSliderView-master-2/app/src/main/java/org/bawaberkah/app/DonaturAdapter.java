package org.bawaberkah.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.bawaberkah.app.R;

import java.util.List;

public class DonaturAdapter extends RecyclerView.Adapter<DonaturAdapter.DonaturViewHolder> {
    private Context mCtx;
    private List<Donatur> donaturs;

    public DonaturAdapter(Context mCtx, List<Donatur> donaturs) {
        this.mCtx = mCtx;
        this.donaturs = donaturs;
    }

    @Override
    public DonaturViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.row_donatur, null);
        return new DonaturViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DonaturAdapter.DonaturViewHolder holder, int position) {
        Donatur donatur = donaturs.get(position);

        holder.idDonatur.setText(donatur.getIdDonatur());
        holder.nama.setText(donatur.getNama());
        holder.nominal.setText(donatur.getNominal());
        holder.catatan.setText(donatur.getCatatan());
    }

    @Override
    public int getItemCount() {
        return donaturs.size();
    }

    class DonaturViewHolder extends RecyclerView.ViewHolder {

        TextView idDonatur, nama, nominal, catatan;

        public DonaturViewHolder(View itemView) {
            super(itemView);

            idDonatur = (TextView) itemView.findViewById(R.id.lblId);
            nama = (TextView) itemView.findViewById(R.id.lblDonatur);
            nominal = (TextView) itemView.findViewById(R.id.lblNominal);
            catatan = (TextView) itemView.findViewById(R.id.lblCatatan);
        }
    }

}
