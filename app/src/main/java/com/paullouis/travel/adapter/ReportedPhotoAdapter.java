package com.paullouis.travel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.paullouis.travel.R;
import com.paullouis.travel.model.ReportedPhoto;
import java.util.List;

public class ReportedPhotoAdapter extends RecyclerView.Adapter<ReportedPhotoAdapter.ReportViewHolder> {

    private List<ReportedPhoto> reports;

    public ReportedPhotoAdapter(List<ReportedPhoto> reports) {
        this.reports = reports;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reported_photo, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportedPhoto report = reports.get(position);
        
        holder.tvReason.setText(report.getReason());
        holder.tvInfo.setText("Signalé par " + report.getReporterName() + " le " + report.getDate());
        holder.tvStatus.setText(report.getStatus().name());

        Glide.with(holder.itemView.getContext())
                .load(report.getPhoto().getImageUrl())
                .centerCrop()
                .into(holder.ivPhoto);

        holder.btnKeep.setOnClickListener(v -> {
            reports.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reports.size());
        });

        holder.btnDelete.setOnClickListener(v -> {
            reports.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, reports.size());
        });
    }

    @Override
    public int getItemCount() {
        return reports.size();
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        TextView tvReason, tvInfo, tvStatus;
        View btnKeep, btnDelete;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivReportedPhoto);
            tvReason = itemView.findViewById(R.id.tvReportReason);
            tvInfo = itemView.findViewById(R.id.tvReporterInfo);
            tvStatus = itemView.findViewById(R.id.tvReportStatus);
            btnKeep = itemView.findViewById(R.id.btnKeep);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
