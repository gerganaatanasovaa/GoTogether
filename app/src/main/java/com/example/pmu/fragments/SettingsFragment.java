package com.example.pmu.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.widget.LinearLayout;

import android.widget.Toast;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.models.User;
import com.example.pmu.utils.LanguageManager;
import com.google.firebase.auth.FirebaseAuth;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import java.util.Locale;

@EFragment(R.layout.fragment_settings)
public class SettingsFragment extends BaseFragment {

    @ViewById
    LinearLayout languageOption, themeOption, logoutOption;

    @AfterViews
    void init() {
        setupLanguageOption();
        setupLogoutOption();
        setupThemeOption();
    }
    private void setupLanguageOption() {
        languageOption.setOnClickListener(v -> {
            String newLang = Locale.getDefault().getLanguage().equals("bg") ? "en" : "bg";
            Locale locale = new Locale(newLang);
            Locale.setDefault(locale);
            LanguageManager.changeLanguage(getActivity());
            Configuration config = new Configuration();
            config.setLocale(locale);

            getActivity().getResources().updateConfiguration(config, getActivity().getResources().getDisplayMetrics());

            getActivity().recreate();
        });
    }

    private void setupThemeOption() {
        themeOption.setOnClickListener(v -> {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            boolean isDark = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

            AppCompatDelegate.setDefaultNightMode(isDark ?
                    AppCompatDelegate.MODE_NIGHT_NO :
                    AppCompatDelegate.MODE_NIGHT_YES);

            getActivity().recreate();
        });
    }

    private void setupLogoutOption() {
        logoutOption.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();
            User.setInstance(null);
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();

            ((MainActivity) requireActivity()).addFragmentAndClearBackstack(new LoginFragment_());
        });
    }
}