package com.example.pmu.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.interfaces.ChatClickListener;
import com.example.pmu.models.ConversationModel;
import com.example.pmu.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationAdapter
        extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private final List<ConversationModel> conversationList;
    private final ChatClickListener clickListener;

    public ConversationAdapter(List<ConversationModel> conversationList,
                               ChatClickListener clickListener) {
        this.conversationList = conversationList;
        this.clickListener    = clickListener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_preview, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ConversationViewHolder holder, int position) {
        ConversationModel convo = conversationList.get(position);
        User user               = convo.getUser();
        String name             = user.getFirstName();
        String imageUrl         = user.getProfileImageUrl();
        String lastMsg          = convo.getLastMessage().getText();

        holder.userNameTextView.setText(name);
        holder.lastMessageTextView.setText(lastMsg);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.userImageView);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(convo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImageView;
        TextView        userNameTextView;
        TextView        lastMessageTextView;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageView      = itemView.findViewById(R.id.userImageView);
            userNameTextView   = itemView.findViewById(R.id.userNameTextView);
            lastMessageTextView= itemView.findViewById(R.id.lastMessageTextView);
        }
    }
}