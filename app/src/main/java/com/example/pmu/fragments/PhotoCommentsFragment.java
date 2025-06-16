package com.example.pmu.fragments;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.adapters.CommentsAdapter;
import com.example.pmu.interfaces.CommentsListener;
import com.example.pmu.interfaces.SimpleCallback;
import com.example.pmu.models.CommentModel;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.Timestamp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@EFragment(R.layout.fragment_photo_comments)
public class PhotoCommentsFragment extends BaseFragment {

    @ViewById
    ImageView photoImageView;
    @ViewById
    ImageView deleteImageView;
    @ViewById
    ImageView sendCommentImageView;

    @ViewById
    RecyclerView commentsRecyclerView;

    @ViewById
    EditText commentEditText;

    @FragmentArg
    String photoUrl;

    @FragmentArg
    String ownerId;

    @FragmentArg
    boolean isOwnProfile;

    private List<CommentModel> commentList = new ArrayList<>();
    private CommentsAdapter adapter;

    @AfterViews
    void init() {

        Glide.with(requireContext()).load(photoUrl).into(photoImageView);

        if (isOwnProfile) {
            deleteImageView.setVisibility(View.VISIBLE);
            deleteImageView.setOnClickListener(v -> confirmDelete());
        } else {
            deleteImageView.setVisibility(View.GONE);
        }

        adapter = new CommentsAdapter(getContext(), commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(adapter);


        loadComments();

        sendCommentImageView.setOnClickListener(v -> {
            String text = commentEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                Timestamp created = Timestamp.now();

                RequestBuilder.addComment(photoUrl, text, new SimpleCallback() {
                    @Override
                    public void onSuccess() {
                        CommentModel newComment = new CommentModel();
                        newComment.setImageUrl(photoUrl);
                        newComment.setText(text);
                        newComment.setCreatedAt(created);
                        newComment.setUserId(User.getInstance().getUserId());

                        commentList.add(newComment);
                        adapter.notifyItemInserted(commentList.size() - 1);
                        commentEditText.setText("");
                        commentsRecyclerView.scrollToPosition(commentList.size() - 1);
                    }
                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(getContext(), "Failed to add comment: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void loadComments() {
        RequestBuilder.loadCommentsForPhoto(photoUrl, new CommentsListener() {
            @Override
            public void onSuccess(ArrayList<CommentModel> comments) {
                commentList.clear();
                commentList.addAll(comments);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String error) {
                Toast.makeText(getContext(), "Error loading comments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Photo")
                .setMessage("Are you sure you want to delete this photo?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    RequestBuilder.deletePhoto(photoUrl, ownerId, new SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "Photo deleted", Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        }
                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(getContext(), "Failed to delete photo: " + message, Toast.LENGTH_LONG).show();
                    }
                });
                })
                .setNegativeButton("Cancel", null)
                .show();

}
}