package com.example.pmu.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.text.InputType;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.models.User;
import com.example.pmu.utils.RequestBuilder;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import java.io.ByteArrayOutputStream;

@EFragment(R.layout.fragment_signup)
public class RegisterFragment extends BaseFragment {

    @ViewById
    EditText emailEditText;

    @ViewById
    TextView usernameErrorTextView;
    @ViewById
    EditText firstNameEditText;
    @ViewById
    EditText passwordEditText;
    @ViewById
    TextView passwordErrorTextView;
    @ViewById
    AutoCompleteTextView countryDropdown;
    @ViewById
    EditText confirmPasswordEditText;
    @ViewById
    Button signupButton;
    @ViewById
    TextView incorrectPassword;

    private String email, firstName, password, confirmPassword, country;


    @Override
    public void onResume() {
        super.onResume();

        emailEditText.setOnFocusChangeListener(onFocusChange());
        firstNameEditText.setOnFocusChangeListener(onFocusChange());
        passwordEditText.setOnFocusChangeListener(onFocusChange());
        confirmPasswordEditText.setOnFocusChangeListener(onFocusChange());
        countryDropdown.setOnFocusChangeListener(onFocusChange());

        setupPasswordToggle(passwordEditText);
        setupPasswordToggle(confirmPasswordEditText);
    }

    @Click
    void signupButton() {

        boolean isValid = true;

        email = String.valueOf(emailEditText.getText());
        firstName = String.valueOf(firstNameEditText.getText());
        confirmPassword = String.valueOf(confirmPasswordEditText.getText());
        password = String.valueOf(passwordEditText.getText());
        country = String.valueOf(countryDropdown.getText());

        usernameErrorTextView.setVisibility(View.GONE);
        passwordErrorTextView.setVisibility(View.GONE);
        incorrectPassword.setVisibility(View.GONE);

        resetInputBackgrounds();

        if (email.isEmpty() || !email.contains("@")) {
            usernameErrorTextView.setText(R.string.invalid_email);
            usernameErrorTextView.setVisibility(View.VISIBLE);
            showInputError(emailEditText);
            isValid = false;
        }

        if (password.length() < 6 || password.length() > 10) {
            passwordErrorTextView.setText(R.string.error_password);
            passwordErrorTextView.setVisibility(View.VISIBLE);
            showInputError(passwordEditText);
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            incorrectPassword.setText(R.string.register_error);
            incorrectPassword.setVisibility(View.VISIBLE);
            showInputError(confirmPasswordEditText);
            isValid = false;
        }

        if (!isValid) return;
        String defaultPicture = "https://firebasestorage.googleapis.com/v0/b/gotogether-95141.firebasestorage.app/o/profile_images%2Fperson_50dp_000000_FILL0_wght400_GRAD0_opsz48.svg?alt=media&token=21b281ba-1ccf-4a15-af50-563afd1259b7";

        String password = passwordEditText.getText().toString().trim();

        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setCountry(country);
        user.setBio("");
        user.setProfileImageUrl(defaultPicture);

        RequestBuilder.register(requireContext(), user, password, new LoginAndRegisterListener() {
            @Override
            public void onSuccess() {
                addFragment(((MainActivity) requireActivity()).homePageFragment);
            }

            @Override
            public void onFailure(String message) {
                incorrectPassword.setText(R.string.incorrect_password);
                incorrectPassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void onErrorResponse(String message) {
                usernameErrorTextView.setText(message);
                usernameErrorTextView.setVisibility(View.VISIBLE);
                showInputError(emailEditText);
            }
        });

    }

    private void showInputError(EditText field) {
        field.setBackgroundResource(R.drawable.round_input_error);
    }

    private void resetInputBackgrounds() {
        emailEditText.setBackgroundResource(R.drawable.round_input);
        passwordEditText.setBackgroundResource(R.drawable.round_input);
        confirmPasswordEditText.setBackgroundResource(R.drawable.round_input);
    }


    @AfterViews
    void setupCountryDropdown() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.countries,
                android.R.layout.simple_dropdown_item_1line
        );
        countryDropdown.setAdapter(adapter);
        countryDropdown.setThreshold(1);
    }

    @Click
    void countryDropdown() {
        countryDropdown.showDropDown();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle(EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // index 2 = drawableEnd
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (editText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    } else {

                        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                    }
                    editText.setSelection(editText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    @Click
    void loginLinkTextView(){
        addFragment(new LoginFragment_());
    }

}

