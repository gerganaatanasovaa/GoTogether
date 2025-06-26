package com.example.pmu.fragments;

import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.pmu.R;
import com.google.firebase.auth.FirebaseAuth;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_forgot_pass)
public class ForgotPasswordFragment extends BaseFragment {

    @ViewById
    EditText emailEditText;

    @ViewById
    Button sendEmailButton;

    @Click(R.id.sendEmailButton)
    void onSendEmailClicked() {
        String email = emailEditText.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        sendEmailButton.setEnabled(false);

        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Check your email to reset your password", Toast.LENGTH_LONG).show();

                    addFragment(new LoginFragment_());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to send email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    sendEmailButton.setEnabled(true);
                });
    }
}
