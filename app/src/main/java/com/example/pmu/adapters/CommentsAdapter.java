package com.example.pmu.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.interfaces.SimpleCallback;
import com.example.pmu.interfaces.UserFetchListener;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>{
    private Context context;
    private List<CommentModel> commentList;
    private final Map<String, User> userCache = new HashMap<>();

    public CommentsAdapter(Context context, List<CommentModel> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CommentModel comment = commentList.get(position);
        holder.commentTextView.setText(comment.getText());

        String userId = comment.getUserId();

        if (userCache.containsKey(userId)) {

            User user = userCache.get(userId);
            bindUserInfo(holder, user);
        } else {
            RequestBuilder.getUserById(userId, new UserFetchListener() {
                @Override
                public void onSuccess(User user) {
                    userCache.put(userId, user);
                    bindUserInfo(holder, user);
                }

                @Override
                public void onFailure(String errorMessage) {
                    holder.firstNameTextView.setText("Unknown");
                }
            });
        }
        holder.itemView.setOnLongClickListener(v -> {
            String currentUserId = User.getInstance().getUserId();
            if (currentUserId.equals(comment.getUserId())) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete Comment")
                        .setMessage("Are you sure you want to delete this comment?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            RequestBuilder.deleteComment(comment.getId(), new SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    commentList.remove(position);
                                    notifyItemRemoved(position);
                                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(context, "Failed to delete comment: " + message, Toast.LENGTH_LONG).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            return true;
        });
    }

    private void bindUserInfo(CommentViewHolder holder, User user) {
        holder.firstNameTextView.setText(user.getFirstName());

        Glide.with(context)
                .load(user.getProfileImageUrl())
                .placeholder(R.drawable.ic_nav_profile)
                .circleCrop()
                .into(holder.userImageView);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        TextView firstNameTextView;
        ImageView userImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            userImageView = itemView.findViewById(R.id.userImageView);
        }
    }
}
