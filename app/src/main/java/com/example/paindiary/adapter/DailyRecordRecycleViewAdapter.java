package com.example.paindiary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindiary.R;
import com.example.paindiary.databinding.RvLayoutBinding;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.fragment.DetailFragment;
import com.example.paindiary.converter.DateStringConverter;
import com.example.paindiary.viewmodel.DailyRecordViewModel;

import java.util.List;

public class DailyRecordRecycleViewAdapter extends RecyclerView.Adapter<DailyRecordRecycleViewAdapter.ViewHolder> {
    private List<PainRecord> painRecords;
    private Context context;

    public DailyRecordRecycleViewAdapter(List<PainRecord> painRecords, Context context) {
        this.painRecords = painRecords;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvLayoutBinding binding= RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final PainRecord record = painRecords.get(position);
        String dateText = DateStringConverter.parseDateToStr("dd-MM-yyyy", record.dateEntry);
        viewHolder.binding.tvRvDate.setText(dateText);

        viewHolder.binding.ivItemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                painRecords.remove(record);
                notifyDataSetChanged();
            }
        });

        DailyRecordViewModel model = new ViewModelProvider((FragmentActivity) context).get(DailyRecordViewModel.class);

        viewHolder.binding.ivDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setMessage(record);
                replaceFragment(new DetailFragment());
            }
        });
    }

    public void addPainRecords(List<PainRecord> painRecords) {
        this.painRecords = painRecords;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    private void replaceFragment(Fragment nextFragment) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view, nextFragment);
        fragmentTransaction.commit();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RvLayoutBinding binding;

        public ViewHolder(RvLayoutBinding binding){
            super(binding.getRoot());
            this.binding = binding; }
    }
}
