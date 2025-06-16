package com.example.pmu.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.adapters.ParticipantsAdapter;
import com.example.pmu.interfaces.PlanListener;
import com.example.pmu.interfaces.PlanInfoListener;
import com.example.pmu.interfaces.UserFetchListener;
import com.example.pmu.interfaces.UserJoinStatusListener;
import com.example.pmu.interfaces.UserListener;
import com.example.pmu.models.Plan;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_plan_info)
public class PlanInfoFragment extends BaseFragment {

    private static final int REQUEST_LOCATION_PERMISSION = 101;
    @ViewById
    ImageButton backImageButton;
    @ViewById
    TextView planTitleTextView;
    @ViewById
    ImageView planImageView;
    @ViewById
    TextView planLocationTextView;
    @ViewById
    TextView planDateTextView;
    @ViewById
    TextView planDescriptionTextView;
    @ViewById
    ImageView profilePictureImageView;
    @ViewById
    TextView firstNameTextView;
    @ViewById
    Button joinButton;
    @FragmentArg
    String tripId;
    @ViewById
    RecyclerView rvParticipants;

    private String currentUserId = User.getInstance().getUserId();
    private FusedLocationProviderClient fusedLocationClient;
    private Plan currentPlan;


    @AfterViews
    void init() {
        rvParticipants.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        loadTripInfo();

        backImageButton.setOnClickListener(v -> requireActivity().onBackPressed());

        joinButton.setOnClickListener(v -> {
            if (currentPlan != null && currentUserId.equals(currentPlan.getUserId())) {
                deleteTrip(tripId);
            } else if (joinButton.getText().toString().equals(getString(R.string.join))) {
                RequestBuilder.joinPlan(currentUserId, tripId, new PlanListener() {
                    @Override
                    public void onSuccess() {
                        joinButton.setText(R.string.unjoin);
                        Toast.makeText(getActivity(), "You joined this trip!", Toast.LENGTH_SHORT).show();
                        UpcomingTripsFragment upcomingTripsFragment = new UpcomingTripsFragment_();
                        addFragment(upcomingTripsFragment);
                    }

                    @Override
                    public void onFailure(String message) {
                        showErrorAlertDialog(message);
                    }
                });
            } else {
                RequestBuilder.leavePlan(currentUserId, tripId, new PlanListener() {
                    @Override
                    public void onSuccess() {
                        joinButton.setText(R.string.join);
                        Toast.makeText(getActivity(), "You left this trip.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        showErrorAlertDialog(message);
                    }
                });
            }
        });
    }

    private void loadTripInfo() {
        RequestBuilder.getPlanById(tripId, new PlanInfoListener() {
            @Override
            public void onSuccess(Plan plan) {
                currentPlan = plan;

                planTitleTextView.setText(plan.getTitle());
                planDescriptionTextView.setText(plan.getDescription());
                planDateTextView.setText(plan.getDate());
                planLocationTextView.setText(plan.getDestination());
                planLocationTextView.setPaintFlags(
                        planLocationTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG
                );

                Glide.with(requireContext()).load(plan.getImageUrl()).into(planImageView);

                loadCreatorName(plan.getUserId());
                loadParticipants(plan.getParticipants());

                if (currentUserId.equals(plan.getUserId())) {
                    joinButton.setText(R.string.delete);
                } else {
                    checkParticipation();
                }
            }

            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });
    }

    private void loadCreatorName(String userId) {
        RequestBuilder.getUserById(userId, new UserFetchListener() {
            @Override
            public void onSuccess(User user) {
                firstNameTextView.setText(user.getFirstName());
                firstNameTextView.setOnClickListener(v -> {
                    ProfileFragment_ profileFragment = new ProfileFragment_();
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", user.getUserId());
                    profileFragment.setArguments(bundle);
                    addFragment(profileFragment);
                });
                Glide.with(requireContext())
                        .load(user.getProfileImageUrl())
                        .placeholder(R.drawable.ic_placeholder)
                        .into(profilePictureImageView);
            }

            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });
    }

    private void deleteTrip(String tripId) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_trip_title)
                .setMessage(R.string.delete_trip_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    RequestBuilder.deleteTrip(tripId, new PlanListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), R.string.trip_deleted, Toast.LENGTH_SHORT).show();
                            requireActivity().onBackPressed();
                        }

                        @Override
                        public void onFailure(String message) {
                            showErrorAlertDialog(message);
                        }
                    });
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void checkParticipation() {
        RequestBuilder.checkUserParticipation(currentUserId, tripId, new UserJoinStatusListener() {
            @Override
            public void onResult(boolean isParticipant) {
                if (isParticipant) {
                    joinButton.setText(R.string.unjoin);
                } else {
                    joinButton.setText(R.string.join);
                }
            }

            @Override
            public void onFailure(String message) {
                showErrorAlertDialog(message);
            }
        });
    }

    private void loadParticipants(List<String> participantIds) {
        RequestBuilder.getUsersByIds(participantIds, new UserListener() {
            @Override
            public void onSuccess(List<User> users) {
                ParticipantsAdapter adapter = new ParticipantsAdapter(
                        users,
                        user -> {
                            ProfileFragment_ pf = new ProfileFragment_();
                            Bundle b = new Bundle();
                            b.putString("userId", user.getUserId());
                            pf.setArguments(b);
                            addFragment(pf);
                        }
                );
                rvParticipants.setAdapter(adapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(),
                                "Failed to load participants: " + errorMessage,
                                Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Click(R.id.planLocationTextView)
    void onPlanLocationClicked() {
        // 1) Check permission
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{ Manifest.permission.ACCESS_FINE_LOCATION },
                    REQUEST_LOCATION_PERMISSION
            );
            return;
        }

        // 2) Permission granted → get last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        launchDrivingDirections(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Couldn't get current location",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            getContext(),
                            "Error getting location: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // retry
                onPlanLocationClicked();
            } else {
                Toast.makeText(
                        getContext(),
                        "Location permission is needed to show directions",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void launchDrivingDirections(double lat, double lon) {
        String origin      = lat + "," + lon;
        String destination = Uri.encode(planLocationTextView.getText().toString());

        Uri uri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1" +
                        "&origin=" + origin +
                        "&destination=" + destination +
                        "&travelmode=driving"
        );

        Intent intent = new Intent(Intent.ACTION_VIEW, uri)
                .setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(
                requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(
                    getContext(),
                    "Google Maps not installed",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

}


