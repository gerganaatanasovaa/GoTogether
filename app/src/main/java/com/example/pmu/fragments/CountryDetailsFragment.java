package com.example.pmu.fragments;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.interfaces.PlanSearchListener;
import com.example.pmu.models.Plan;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EFragment(R.layout.fragment_country_details)
public class CountryDetailsFragment extends BaseFragment {

    @ViewById
    ImageView countryImageView;

    @ViewById
    TextView countryNameTextView;

    @ViewById
    TextView countryDescriptionTextView;

    @ViewById
    Button viewPlansButton;

    @ViewById
    ImageButton backImageButton;


    @AfterViews
    void init() {
        if (getArguments() != null) {
            String name = getArguments().getString("name");
            String description = getArguments().getString("description");
            String imageUrl = getArguments().getString("imageUrl");

            countryNameTextView.setText(name);
            countryDescriptionTextView.setText(description);

            Glide.with(requireContext())
                    .load(imageUrl)
                    .centerCrop()
                    .into(countryImageView);
        }
    }

    @Click(R.id.backImageButton)
    void onBackClicked() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    @Click(R.id.viewPlansButton)
    void onSearchPlansClicked() {
        if (getArguments() == null) return;

        String selectedDestination = getArguments().getString("name");

        RequestBuilder.getPlansForSuggestedDestination(selectedDestination, new PlanSearchListener() {
            @Override
            public void onSuccess(List<Plan> plans) {
                if (plans.isEmpty()) {
                    Toast.makeText(getContext(), "No plans found!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Bundle bundle = new Bundle();
                bundle.putString("destination", selectedDestination);
                bundle.putSerializable("plansList", new ArrayList<>(plans));

                FilteredPlansFragment_ fragment = new FilteredPlansFragment_();
                fragment.setArguments(bundle);
                addFragment(fragment);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error loading plans: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}





