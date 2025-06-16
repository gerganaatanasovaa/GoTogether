package com.example.pmu.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.models.User;

import java.util.List;

public class ParticipantsAdapter
        extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {

    public interface OnParticipantClickListener {
        void onParticipantClick(User user);
    }

    private final List<User> participants;
    private final OnParticipantClickListener listener;

    public ParticipantsAdapter(List<User> participants,
                               OnParticipantClickListener listener) {
        this.participants = participants;
        this.listener     = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_participant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        User user = participants.get(position);

        Glide.with(holder.imageView.getContext())
                .load(user.getProfileImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_nav_profile)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onParticipantClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.participantImageView);
        }
    }
}
