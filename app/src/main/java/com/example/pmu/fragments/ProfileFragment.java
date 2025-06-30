package com.example.pmu.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.adapters.PhotoAdapter;
import com.example.pmu.interfaces.ImageUploadListener;

import com.example.pmu.interfaces.UserFetchListener;
import com.example.pmu.models.Photo;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EFragment(R.layout.fragment_profile)
public class ProfileFragment extends BaseFragment {

    @ViewById TextView nameTextView;
    @ViewById TextView countryNameTextView;
    @ViewById TextView bioTextView;
    @ViewById Button sendMessageButton;
    @ViewById RecyclerView photosRecycler;
    @ViewById ImageView profileImageView;
    @ViewById ImageButton addPhotoButton;
    @ViewById ImageButton uploadProfileImageButton;
    @ViewById ImageButton editBioButton;

    @FragmentArg String userId;

    FirebaseFirestore db;
    FirebaseUser currentUser;
    String viewedUserId;
    boolean isOwnProfile;

    private List<Photo> photoList = new ArrayList<>();
    private PhotoAdapter photoAdapter;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private static final String TAG = "ProfileFragment";

    @AfterViews
    void initProfile() {
        setHasOptionsMenu(true);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getContext(), "No logged-in user", Toast.LENGTH_SHORT).show();
            return;
        }

        viewedUserId = getArguments() != null ? getArguments().getString("userId") : currentUser.getUid();
        isOwnProfile = currentUser.getUid().equals(viewedUserId);

        initActivityResultLaunchers();

        photoAdapter = new PhotoAdapter(getContext(), photoList, photoUrl -> {
            PhotoCommentsFragment fragment = PhotoCommentsFragment_.builder()
                    .photoUrl(photoUrl)
                    .ownerId(viewedUserId)
                    .isOwnProfile(isOwnProfile)
                    .build();
            ((MainActivity) requireActivity()).addFragment(fragment);
        });

        photosRecycler.setAdapter(photoAdapter);
        photosRecycler.setLayoutManager(new GridLayoutManager(getContext(), 3));

        updateVisibility();
        initClickListeners();

        RequestBuilder.getUserById(viewedUserId, new UserFetchListener() {
            @Override
            public void onSuccess(User user) {
                nameTextView.setText(user.getFirstName());
                countryNameTextView.setText(user.getCountry());
                bioTextView.setText(user.getBio());

                String profileImageUrl = user.getProfileImageUrl();
                if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                    Glide.with(requireContext()).load(profileImageUrl).into(profileImageView);
                }

                RequestBuilder.loadUserPhotos(viewedUserId, photos -> {
                    photoList.clear();
                    photoList.addAll(photos);
                    photoAdapter.notifyDataSetChanged();
                }, e -> {
                    Toast.makeText(getContext(), "Failed to load photos.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initActivityResultLaunchers() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        RequestBuilder.uploadProfileImage(currentUser.getUid(), imageUri, getContext(), new ImageUploadListener() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                Glide.with(requireContext())
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(profileImageView);
                                Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        RequestBuilder.uploadGalleryPhoto(currentUser.getUid(), imageUri, getContext(), new ImageUploadListener() {
                            @Override
                            public void onSuccess(String imageUrl) {
                                photoList.add(0, new Photo(imageUrl, new Timestamp(new Date())));
                                photoAdapter.notifyItemInserted(0);
                                Toast.makeText(getContext(), "Photo uploaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(getContext(), "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    private void initClickListeners() {
        uploadProfileImageButton.setOnClickListener(v -> uploadProfileImageButton());
        editBioButton.setOnClickListener(v -> showEditBioDialog());
        addPhotoButton.setOnClickListener(v -> addPhotoClicked());
        sendMessageButton.setOnClickListener(v -> sendMessageClicked());
    }

    private void updateVisibility() {
        if (isOwnProfile) {
            uploadProfileImageButton.setVisibility(View.VISIBLE);
            editBioButton.setVisibility(View.VISIBLE);
            addPhotoButton.setVisibility(View.VISIBLE);
            sendMessageButton.setText(R.string.view_mess);
        } else {
            uploadProfileImageButton.setVisibility(View.GONE);
            editBioButton.setVisibility(View.GONE);
            addPhotoButton.setVisibility(View.GONE);
            sendMessageButton.setText(R.string.send_message);
        }
    }

    private void uploadProfileImageButton() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void addPhotoClicked() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void showEditBioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Bio");

        final EditText input = new EditText(getContext());
        input.setText(bioTextView.getText().toString());
        input.setSelection(input.getText().length());

        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newBio = input.getText().toString().trim();
            if (!newBio.isEmpty()) {
                RequestBuilder.updateUserBio(currentUser.getUid(), newBio, task -> {
                    if (task.isSuccessful()) {
                        bioTextView.setText(newBio);
                        Toast.makeText(getContext(), "Bio updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to update bio", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "Bio cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void sendMessageClicked() {
        if (currentUser == null || viewedUserId == null) return;

        if (isOwnProfile) {
            AllChatsFragment allChatsFragment = new AllChatsFragment_();
            ((MainActivity) requireActivity()).addFragment(allChatsFragment);
        } else {
            ChatFragment fragment = ChatFragment_.builder()
                    .receiverId(viewedUserId)
                    .build();
            ((MainActivity) requireActivity()).addFragment(fragment);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshPhotos();
    }

    private void refreshPhotos() {
        RequestBuilder.loadUserPhotos(viewedUserId, photos -> {
            photoList.clear();
            photoList.addAll(photos);
            photoAdapter.notifyDataSetChanged();
        }, e -> {
            Toast.makeText(getContext(), "Failed to load photos.", Toast.LENGTH_SHORT).show();
        });
    }
}
