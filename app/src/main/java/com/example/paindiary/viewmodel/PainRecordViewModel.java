package com.example.paindiary.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.paindiary.dao.PainRecordDAO;
import com.example.paindiary.entity.PainRecord;
import com.example.paindiary.repository.PainRecordRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private PainRecordRepository painRecordRepository;
    private LiveData<List<PainRecord>> allPainRecords;
    private LiveData<List<PainRecordDAO.PainLocationFrequency>> pLocationFreq;

    public PainRecordViewModel(@NonNull Application application) {
        super(application);
        painRecordRepository = new PainRecordRepository(application);
        allPainRecords = painRecordRepository.getAllPainRecords();
        pLocationFreq = painRecordRepository.getFrequencyGroupedByPainLocation();
    }

    public void delete(PainRecord painRecord) {
        painRecordRepository.delete(painRecord);
    }

    public void deleteAll() {
        painRecordRepository.deleteAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByDateFuture(final long painRecordDate){
        return painRecordRepository.findByDateFuture(painRecordDate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int painRecordId){
        return painRecordRepository.findByIdFuture(painRecordId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByUserEmailFuture(final String painRecordUserEmail){
        return painRecordRepository.findByUserEmailFuture(painRecordUserEmail);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<List<PainRecord>> findPainRecordBetweenDate(final long start, long end){
        return painRecordRepository.findPainRecordBetweenDate(start, end);
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public LiveData<List<PainRecordDAO.PainLocationFrequency>> getFrequencyGroupedByPainLocation() {
        return pLocationFreq;
    }

    public void insert(PainRecord painRecord) {
        painRecordRepository.insert(painRecord);
    }

    public void update(PainRecord painRecord) {
        painRecordRepository.updatePainRecord(painRecord);
    }
}
