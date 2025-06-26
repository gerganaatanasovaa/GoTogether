package com.example.pmu.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmu.R;
import com.example.pmu.adapters.DestinationAdapter;
import com.example.pmu.adapters.PlansAdapter;
import com.example.pmu.interfaces.PlanSearchListener;
import com.example.pmu.models.Plan;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_search_results)
public class FilteredPlansFragment extends BaseFragment{

    @ViewById
    TextView helloTextView;
    @ViewById
    EditText searchEditText;
    @ViewById
    RecyclerView recyclerViewPlans;

    @FragmentArg
    String destination;

    @FragmentArg
    String arrivalDate;

    @FragmentArg
    String departureDate;

    private PlansAdapter plansAdapter;

    @AfterViews
    void init() {
        setupSearch();

        String firstName = User.getInstance().getFirstName();
        helloTextView.setText(getString(R.string.hello, firstName));
        recyclerViewPlans.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null && getArguments().containsKey("plansList")) {
            loadPlansFromBundle();
        } else {
            loadFilteredPlans();
        }
    }

    private void setupSearch() {
        searchEditText.setFocusable(false);
        searchEditText.setClickable(true);
        searchEditText.setOnClickListener(view -> {
            addFragment(new SearchPlanFragment_());
        });
    }

    private void loadFilteredPlans() {
        RequestBuilder.searchPlans(destination, arrivalDate, departureDate, new PlanSearchListener() {
            @Override
            public void onSuccess(List<Plan> plans) {
                if (plans.isEmpty()) {
                    Toast.makeText(getContext(), "No plans found.", Toast.LENGTH_SHORT).show();
                    return;
                }

                plansAdapter = new PlansAdapter(getContext(), plans, plan -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("tripId", plan.getTripId());

                    PlanInfoFragment_ fragment = new PlanInfoFragment_();
                    fragment.setArguments(bundle);

                    addFragment(fragment);
                });
                recyclerViewPlans.setAdapter(plansAdapter);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void loadPlansFromBundle() {
        List<Plan> receivedPlans = (List<Plan>) getArguments().getSerializable("plansList");

        if (receivedPlans == null || receivedPlans.isEmpty()) {
            Toast.makeText(getContext(), "No plans to display", Toast.LENGTH_SHORT).show();
            return;
        }

        plansAdapter = new PlansAdapter(getContext(), receivedPlans, plan -> {
            Bundle bundle = new Bundle();
            bundle.putString("tripId", plan.getTripId());

            PlanInfoFragment_ fragment = new PlanInfoFragment_();
            fragment.setArguments(bundle);

            addFragment(fragment);
        });

        recyclerViewPlans.setAdapter(plansAdapter);
    }
}


