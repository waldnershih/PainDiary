package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.adapter.DailyRecordRecycleViewAdapter;
import com.example.paindiary.databinding.DailyRecordFragmentBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.viewmodel.PainRecordViewModel;

import java.util.List;

public class DailyRecordFragment extends Fragment {
    private DailyRecordFragmentBinding dailyRecordBinding;
    private RecyclerView.LayoutManager layoutManager;
    private DailyRecordRecycleViewAdapter adapter;
    private PainRecordViewModel painRecordViewModel;

    public DailyRecordFragment(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dailyRecordBinding = DailyRecordFragmentBinding.inflate(inflater, container, false);
        View view = dailyRecordBinding.getRoot();

        getActivity().setTitle("Daily Record");

        painRecordViewModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

        painRecordViewModel.getAllPainRecords().observe(getViewLifecycleOwner(), new Observer<List<PainRecord>>() {
            @Override
            public void onChanged(List<PainRecord> painRecords) {
                if (painRecords != null) {
                    if (painRecords.size() != 0) {
                        adapter = new DailyRecordRecycleViewAdapter(painRecords, getActivity());
                        dailyRecordBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                        dailyRecordBinding.recyclerView.setAdapter(adapter);
                        layoutManager = new LinearLayoutManager(getActivity());
                        dailyRecordBinding.recyclerView.setLayoutManager(layoutManager);
                    } else {
                        dailyRecordBinding.recyclerView.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(getActivity(), "Pain Records did not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        dailyRecordBinding = null;
    }
}