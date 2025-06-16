package com.example.pmu.fragments;

import android.widget.EditText;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmu.R;
import com.example.pmu.adapters.DestinationAdapter;
import com.example.pmu.interfaces.DestinationClickListener;
import com.example.pmu.interfaces.DestinationListener;
import com.example.pmu.models.Destination;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_homepage)
public class HomePageFragment extends BaseFragment {

    @ViewById
    TextView helloTextView;

    @ViewById
    EditText searchEditText;

    @ViewById
    RecyclerView recyclerDestinations;

    private List<Destination> destinationList = new ArrayList<>();
    private DestinationAdapter adapter;

    @AfterViews
    void init() {
        String firstName = User.getInstance().getFirstName();
        String greeting = getString(R.string.hello, firstName);
        helloTextView.setText(greeting);

        setupSearch();
        setupRecyclerView();
        loadDestinationsFromFirebase();

    }

    private void setupSearch() {
        searchEditText.setFocusable(false);
        searchEditText.setClickable(true);
        searchEditText.setOnClickListener(view -> {
            addFragment(new SearchPlanFragment_());
        });
    }

    private void setupRecyclerView() {
        adapter = new DestinationAdapter(getContext(), destinationList, new DestinationClickListener() {
            @Override
            public void onSuccess(Destination destination) {
                openDetailsFragment(destination);
            }

            @Override
            public void onFailure(String message) {

            }
        });
        recyclerDestinations.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDestinations.setAdapter(adapter);


    }

    private void loadDestinationsFromFirebase() {
            RequestBuilder.getDestinations(new DestinationListener() {
                @Override
                public void onSuccess(List<Destination> destinations) {
                    destinationList.clear();
                    destinationList.addAll(destinations);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void openDetailsFragment(Destination destination) {
        Bundle bundle = new Bundle();
        bundle.putString("name", destination.getName());
        bundle.putString("description", destination.getDescription());
        bundle.putString("imageUrl", destination.getImageUrl());

        CountryDetailsFragment_ fragment = new CountryDetailsFragment_();
        fragment.setArguments(bundle);
        addFragment(fragment);
    }
}

