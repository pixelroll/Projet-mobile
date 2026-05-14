package com.paullouis.travel.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.paullouis.travel.ItineraryDetailActivity;
import com.paullouis.travel.R;
import com.paullouis.travel.model.SavedItinerary;

import java.util.List;

public class ProfileItineraryAdapter extends RecyclerView.Adapter<ProfileItineraryAdapter.ViewHolder> {

    private List<SavedItinerary> items;
    private Context context;

    public ProfileItineraryAdapter(List<SavedItinerary> items, Context context) {
        this.items = items;
        this.context = context;
    }

    public void setItineraries(List<SavedItinerary> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_itinerary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedItinerary item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDuration.setText(item.getDurationFormatted());
        holder.tvLocation.setText(item.getLocationName() != null ? item.getLocationName() : "");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItineraryDetailActivity.class);
            intent.putExtra(ItineraryDetailActivity.EXTRA_SAVED_ITINERARY_ID, item.getId());
            context.startActivity(intent);
        });

        if (holder.btnUnlike != null) {
            holder.btnUnlike.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    new android.app.AlertDialog.Builder(context)
                            .setTitle("Retirer des favoris")
                            .setMessage("Êtes-vous sûr de vouloir retirer ce parcours de vos favoris ?")
                            .setPositiveButton("Retirer", (dialog, which) -> {
                                com.paullouis.travel.data.FirebaseRepository.getInstance().deleteItinerary(item.getId(), new com.paullouis.travel.data.DataCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        items.remove(currentPos);
                                        notifyItemRemoved(currentPos);
                                        notifyItemRangeChanged(currentPos, items.size());
                                        Toast.makeText(context, "Parcours retiré des favoris", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Toast.makeText(context, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            })
                            .setNegativeButton("Annuler", null)
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDuration, tvLocation;
        ImageView btnUnlike;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            btnUnlike = itemView.findViewById(R.id.btnUnlike);
        }
    }
}
