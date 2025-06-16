package com.example.pmu.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pmu.R;
import com.example.pmu.adapters.CommentsAdapter;
import com.example.pmu.models.CommentModel;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_comments)
public class CommentsFragment extends BaseFragment {

    @ViewById
    RecyclerView commentsRecyclerView;
    @ViewById
    ImageView selectImageView;
    @ViewById
    EditText commentEditText;
    @ViewById
    ImageView sendCommentImageView;

    private static final int PICK_IMAGE_REQUEST = 101;
    private Uri selectedImageUri = null;

    private List<CommentModel> commentList = new ArrayList<>();
    private CommentsAdapter commentAdapter;
    private String tripId;

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    @AfterViews
    void setup() {
        commentAdapter = new CommentsAdapter(getContext(), commentList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentsRecyclerView.setAdapter(commentAdapter);

        RequestBuilder.getCommentsForTrip(tripId, (value, error) -> {
            if (error != null || value == null) return;

            commentList.clear();
            for (DocumentSnapshot doc : value.getDocuments()) {
                CommentModel comment = doc.toObject(CommentModel.class);
                commentList.add(comment);
            }
            commentAdapter.notifyDataSetChanged();
        });

        selectImageView.setOnClickListener(v -> openImagePicker());
        sendCommentImageView.setOnClickListener(v -> sendComment());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Toast.makeText(getContext(), "Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendComment() {
        String commentText = commentEditText.getText().toString().trim();
        if (commentText.isEmpty() && selectedImageUri == null) {
            Toast.makeText(getContext(), "Write a comment or select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        sendCommentImageView.setEnabled(false);

        if (selectedImageUri != null) {
            RequestBuilder.uploadCommentImage(
                    selectedImageUri,
                    imageUrl -> postComment(commentText, imageUrl.toString()),
                    error -> {
                        Toast.makeText(getContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                        sendCommentImageView.setEnabled(true);
                    }
            );
        } else {
            postComment(commentText, "");
        }
    }

    private void postComment(String text, String imageUrl) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Timestamp date = Timestamp.now();

        CommentModel comment = new CommentModel(date, imageUrl, text, userId);

        RequestBuilder.postComment(
                comment,
                docRef -> {
                    commentEditText.setText("");
                    selectedImageUri = null;
                    sendCommentImageView.setEnabled(true);
                    Toast.makeText(getContext(), "Comment posted", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    Toast.makeText(getContext(), "Failed to post comment", Toast.LENGTH_SHORT).show();
                    sendCommentImageView.setEnabled(true);
                }
        );
    }
}