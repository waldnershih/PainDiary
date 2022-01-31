package com.example.paindiary.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.paindiary.databinding.WorkManagerFragmentBinding;
import com.example.paindiary.viewmodel.PainRecordViewModel;
import com.example.paindiary.viewmodel.WorkManagerViewModel;

public class WorkManagerFragment extends Fragment {

    private WorkManagerFragmentBinding wBinding;

    private PainRecordViewModel prModel;

    private WorkManagerViewModel wmModel;

    public WorkManagerFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        wBinding = WorkManagerFragmentBinding.inflate(inflater, container, false);
        View view = wBinding.getRoot();

        getActivity().setTitle("Work Manager");

        prModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(PainRecordViewModel.class);

//        homeBinding.defaultButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DefaultValue defaultValue = new DefaultValue();
//                List<PainRecord> painRecords = defaultValue.getPainRecords();
//
//                for (PainRecord pr : painRecords) {
//                    prModel.insert(pr);
//                }
//                Toast.makeText(getActivity(), "Insert Successfully", Toast.LENGTH_SHORT).show();
//            }
//        });

        wmModel = ViewModelProvider
                .AndroidViewModelFactory
                .getInstance(getActivity().getApplication())
                .create(WorkManagerViewModel.class);

        wBinding.workManagerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wmModel.applyRequest();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        wBinding = null;
    }
}

/**
 * Reference
 * https://developer.android.com/topic/libraries/architecture/workmanager/advanced
 * https://developer.android.com/topic/libraries/architecture/workmanager/advanced
 */
