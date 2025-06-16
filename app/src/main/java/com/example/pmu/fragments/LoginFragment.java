package com.example.pmu.fragments;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pmu.R;
import com.example.pmu.interfaces.LoginAndRegisterListener;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.utils.RequestBuilder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_login)
public class LoginFragment extends BaseFragment {

    @ViewById
    TextView signupLinkView;
    @ViewById
    EditText emailEditText;
    @ViewById
    EditText passwordEditText;
    @ViewById
    Button loginButton;
    @ViewById
    TextView forgotPasswordTextView;
    @ViewById
    TextView errorDataTextView;

    private boolean isPasswordVisible = false;


    @Override
    public void onResume() {
        super.onResume();
        signupLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFragment(new RegisterFragment_());
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFragment(new ForgotPasswordFragment_());
            }
        });

        emailEditText.setOnFocusChangeListener(onFocusChange());
        passwordEditText.setOnFocusChangeListener(onFocusChange());
    }

    @Click
    void loginButton() {

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        errorDataTextView.setVisibility(View.GONE);
        resetInputBackgrounds();

        if (email.isEmpty() || password.isEmpty()) {
            errorDataTextView.setVisibility(View.VISIBLE);
            errorDataTextView.setText(R.string.empty_data);
            showErrorState();
            return;
        }

        RequestBuilder.login(email, password, new LoginAndRegisterListener() {
            @Override
            public void onSuccess() {
                resetInputBackgrounds();
                errorDataTextView.setVisibility(View.GONE);
                addFragment(((MainActivity) requireActivity()).homePageFragment);
            }

            @Override
            public void onFailure(String message) {
                errorDataTextView.setVisibility(View.VISIBLE);
                errorDataTextView.setText(R.string.invalid_data);
                showErrorState();
            }

            @Override
            public void onErrorResponse(String message) {
                errorDataTextView.setVisibility(View.VISIBLE);
                errorDataTextView.setText(message);
                showErrorState();
            }
        });

    }

    @AfterViews
    void afterViews() {
        setupPasswordToggle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupPasswordToggle() {
        passwordEditText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {

                    isPasswordVisible = !isPasswordVisible;

                    if (isPasswordVisible) {
                        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                    } else {
                        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    }

                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    private void showErrorState() {
        emailEditText.setBackgroundResource(R.drawable.round_input_error);
        passwordEditText.setBackgroundResource(R.drawable.round_input_error);
    }

    private void resetInputBackgrounds() {
        emailEditText.setBackgroundResource(R.drawable.round_input);
        passwordEditText.setBackgroundResource(R.drawable.round_input);
    }

}