package com.example.paindiary.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindiary.activity.MainActivity;
import com.example.paindiary.databinding.SignoutFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;

public class SignoutFragment extends Fragment {
    private SignoutFragmentBinding signoutBinding;

    public SignoutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        signoutBinding = SignoutFragmentBinding.inflate(inflater, container, false);
        View view = signoutBinding.getRoot();

            final Intent intent = getActivity().getIntent();
            FirebaseAuth.getInstance().signOut();
            intent.setClass(getActivity(), MainActivity.class);
            startActivity(intent);
        SharedPreferences sharedPref= requireActivity().getApplicationContext().getSharedPreferences("Login", Context.MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        spEditor.putString("Login", "");
        spEditor.apply();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        signoutBinding = null;
    }
}
