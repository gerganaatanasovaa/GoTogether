package com.example.pmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pmu.R;
import com.example.pmu.models.Plan;
import java.util.List;

public class UpcomingTripsAdapter extends RecyclerView.Adapter<UpcomingTripsAdapter.TripViewHolder> {

    private List<Plan> tripList;
    private Context context;
    private OnTripClickListener listener;

    public interface OnTripClickListener {
        void onTripClick(Plan plan);
    }

    public UpcomingTripsAdapter(Context context, List<Plan> tripList, OnTripClickListener listener) {
        this.context = context;
        this.tripList = tripList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upcoming_trips, parent, false);

        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Plan plan = tripList.get(position);
        holder.tripTitleTextView.setText(plan.getTitle());
        holder.tripDateTextView.setText(plan.getDate());

        Glide.with(holder.itemView.getContext())
                .load(plan.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.tripImageView);


        holder.viewTripButton.setOnClickListener(v -> listener.onTripClick(plan));

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tripTitleTextView, tripDateTextView;
        ImageView tripImageView;
        Button viewTripButton;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tripTitleTextView = itemView.findViewById(R.id.tripTitleTextView);
            tripDateTextView = itemView.findViewById(R.id.tripDateTextView);
            tripImageView = itemView.findViewById(R.id.tripImageView);
            viewTripButton = itemView.findViewById(R.id.viewTripButton);
        }
    }
}

