package com.example.pmu.fragments;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmu.R;
import com.example.pmu.adapters.UpcomingTripsAdapter;
import com.example.pmu.interfaces.PlanSearchListener;
import com.example.pmu.models.Plan;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EFragment(R.layout.fragment_upcoming_trips)
public class UpcomingTripsFragment extends BaseFragment  {

    @ViewById
    RecyclerView upcomingTripsRecyclerView;

    private UpcomingTripsAdapter adapter;
    private List<Plan> upcomingTrips = new ArrayList<>();

    @AfterViews
    void init() {
        setupRecyclerView();
        loadUpcomingTrips();

    }

    private void setupRecyclerView() {
        upcomingTripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UpcomingTripsAdapter(getContext(), upcomingTrips, plan -> {
            Bundle bundle = new Bundle();
            bundle.putString("tripId", plan.getTripId());

            PlanInfoFragment fragment = new PlanInfoFragment_();
            fragment.setArguments(bundle);
            addFragment(fragment);
        });
        upcomingTripsRecyclerView.setAdapter(adapter);
    }

    private void loadUpcomingTrips() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        RequestBuilder.getUpcomingTrips(userId, new PlanSearchListener() {
            @Override
            public void onSuccess(List<Plan> plans) {
                upcomingTrips.clear();
                upcomingTrips.addAll(plans);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(getContext(), "Error loading trips: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

