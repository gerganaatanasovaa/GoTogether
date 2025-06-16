package com.example.pmu.fragments;

import com.example.pmu.R;
import com.example.pmu.activity.MainActivity;
import com.example.pmu.fragments.LoginFragment;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;

@EFragment(R.layout.fragment_welcome)
public class WelcomeFragment extends BaseFragment {
    @Click
    void loginButton(){
        addFragment(new LoginFragment_());
    }
    @Click
    void signupButton(){
        addFragment(new RegisterFragment_());
    }
    @Override
    public void onBackPressed(){
        getActivity().finish();
    }
}
