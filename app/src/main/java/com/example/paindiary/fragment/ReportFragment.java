package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.paindiary.R;
import com.example.paindiary.databinding.ReportFragmentBinding;


public class ReportFragment extends Fragment {
    private ReportFragmentBinding reportBinding;

    public ReportFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reportBinding = ReportFragmentBinding.inflate(inflater, container, false);
        View view = reportBinding.getRoot();

        getActivity().setTitle("Report");

        reportBinding.pieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new PieFragment());
            }
        });

        reportBinding.donutPieChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new DonutFragment());
            }
        });

        reportBinding.lineChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new LineFragment());
            }
        });

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportBinding = null;
    }

    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, nextFragment);
        fragmentTransaction.commit();
    }
}