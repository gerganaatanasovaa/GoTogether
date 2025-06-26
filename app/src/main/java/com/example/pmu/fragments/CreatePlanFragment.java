package com.example.pmu.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.ImageUploadListener;
import com.example.pmu.interfaces.PlanListener;
import com.example.pmu.models.Plan;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import com.example.pmu.models.User;

@EFragment(R.layout.fragment_create_plans)
public class CreatePlanFragment extends BaseFragment {

    @ViewById
    EditText planNameEditText;

    @ViewById
    TextView planNameCounterTextView;
    @ViewById
    ImageView imageUploadImageView;

    @ViewById
    EditText aboutTripEditText;

    @ViewById
    TextView textCounterTextView;

    @ViewById
    EditText dateEditText;

    @ViewById
    AutoCompleteTextView autoDestination;

    @ViewById
    Button createButton;

    private Uri selectedImageUri;
    private static final int IMAGE_PICK_CODE = 101;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @AfterViews
    void init() {
        planNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                planNameCounterTextView.setText(s.length() + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        selectedImageUri = uri;
                        Glide.with(this)
                                .load(uri)
                                .into(imageUploadImageView);
                    }
                });
    }

    @Click
    void imageUploadImageView() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select a photo"));
    }

    @AfterViews
    void setupDescriptionCounter() {
        aboutTripEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentLength = s.length();
                textCounterTextView.setText(currentLength + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Click
    void dateEditText() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateEditText.setText(selectedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }

    @AfterViews
    void setupAutoCompleteDestination() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.trips,
                android.R.layout.simple_dropdown_item_1line
        );
        autoDestination.setAdapter(adapter);
        autoDestination.setThreshold(1);
    }

    @Click
    void createButton() {
        Plan newPlan = new Plan();
        newPlan.setTitle(planNameEditText.getText().toString().trim());
        newPlan.setDescription(aboutTripEditText.getText().toString().trim());
        newPlan.setDate(dateEditText.getText().toString().trim());
        newPlan.setDestination(autoDestination.getText().toString().trim());
        newPlan.setParticipants(new ArrayList<>());
        newPlan.setUserId(User.getInstance().getUserId());

        if (newPlan.getTitle().isEmpty()
                || newPlan.getDate().isEmpty()
                || newPlan.getDestination().isEmpty()) {
            Toast.makeText(getContext(),
                            "Please fill all required fields",
                            Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        RequestBuilder.uploadTripImage(selectedImageUri, new ImageUploadListener() {
            @Override
            public void onSuccess(String imageUrl) {
                newPlan.setImageUrl(imageUrl);

                RequestBuilder.createTrip(newPlan, new PlanListener() {
                    @Override
                    public void onSuccess() {
                        clearForm();
                        addFragment(((MainActivity) requireActivity())
                                .homePageFragment);
                    }
                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(requireContext(),
                                        "Failed to create trip: " + message,
                                        Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(requireContext(),
                                errorMessage,
                                Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void clearForm() {
        planNameEditText.setText("");
        aboutTripEditText.setText("");
        dateEditText.setText("");
        autoDestination.setText("");
        planNameCounterTextView.setText(R.string.name_counter);
        textCounterTextView.setText("0/100");
        selectedImageUri = null;
        imageUploadImageView.setImageResource(R.drawable.ic_placeholder);
    }

}
