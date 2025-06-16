package com.example.pmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.models.Plan;

import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.PlanViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Plan plan);
    }

    private final Context context;
    private final List<Plan> plans;
    private final OnItemClickListener listener;

    public PlansAdapter(Context context, List<Plan> plans, OnItemClickListener listener) {
        this.context = context;
        this.plans = plans;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_destinations, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plans.get(position);

        holder.nameTextView.setText(plan.getTitle());
        Glide.with(context).load(plan.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(plan);
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        ImageView imageView;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.destinationNameTextView);
            imageView = itemView.findViewById(R.id.destinationImageView);
        }
    }
}
