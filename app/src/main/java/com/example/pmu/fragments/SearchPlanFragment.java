package com.example.pmu.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.pmu.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@EFragment(R.layout.fragment_search_trips)
public class SearchPlanFragment extends BaseFragment {


    @ViewById
    AutoCompleteTextView destinationNameTextView;

    @ViewById
    EditText dateEditText;

    @ViewById
    EditText departureDateEditText;

    @ViewById
    Button searchButton;

    @AfterViews
    void setupDropdown() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.trips,
                android.R.layout.simple_dropdown_item_1line
        );
        destinationNameTextView.setAdapter(adapter);
        destinationNameTextView.setThreshold(1);
    }

    private Calendar arrivalDate, departureDate;

    @Click
    void dateEditText() {
        showDatePicker((date) -> {
            arrivalDate = date;
            dateEditText.setText(formatDate(date));
            if (departureDate != null && departureDate.before(arrivalDate)) {
                departureDateEditText.setText("");
                departureDate = null;
            }
        });
    }

    @Click
    void departureDateEditText() {
        showDatePicker((date) -> {
            if (arrivalDate != null && date.before(arrivalDate)) {
                Toast.makeText(getContext(), "Departure cannot be before arrival", Toast.LENGTH_SHORT).show();
            } else {
                departureDate = date;
                departureDateEditText.setText(formatDate(date));
            }
        });
    }

    private void showDatePicker(OnDateSetListener listener) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                getContext(),
                (view, year, month, day) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, day);
                    listener.onDateSet(selected);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private interface OnDateSetListener {
        void onDateSet(Calendar date);
    }

    private String formatDate(Calendar date) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.getTime());
    }


    @Click
    void searchButton() {
        String destination = destinationNameTextView.getText().toString().trim();
        String arrival = dateEditText.getText().toString().trim();
        String departure = departureDateEditText.getText().toString().trim();

        if (destination.isEmpty() || arrival.isEmpty() || departure.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle args = new Bundle();
        args.putString("destination", destination);
        args.putString("arrivalDate", arrival);
        args.putString("departureDate", departure);

        FilteredPlansFragment_ fragment = new FilteredPlansFragment_();
        fragment.setArguments(args);
        addFragment(fragment);
    }

}
