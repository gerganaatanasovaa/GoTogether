package com.example.pmu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.interfaces.PhotoClickListener;
import com.example.pmu.models.Photo;

import org.androidannotations.annotations.FragmentArg;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<Photo> photoList;

    private PhotoClickListener listener;

    public PhotoAdapter(Context context, List<Photo> photoList, PhotoClickListener listener) {
        this.context = context;
        this.photoList = photoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        Glide.with(context)
                .load(photo.getImageUrl())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.photoImageView);

        holder.photoImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSuccess(photoList.get(position).getImageUrl());
            }
        });

    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}

