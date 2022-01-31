package com.example.paindiary.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.paindiary.entity.PainRecord;

import java.util.List;

@Dao
public interface PainRecordDAO {

    @Query("SELECT * FROM pain_record ORDER BY date_entry DESC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT pain_location, COUNT(pain_location) as count FROM pain_record GROUP BY pain_location ORDER BY count DESC")
    LiveData<List<PainLocationFrequency>> getFrequencyGroupedByPainLocation();

    @Query("SELECT * FROM pain_record WHERE date_entry BETWEEN :start AND :end")
    List<PainRecord> findAllBetweenDate(long start, long end);

    @Query("SELECT * FROM pain_record WHERE uid = :painRecordId LIMIT 1")
    PainRecord findByID(int painRecordId);

    @Query("SELECT * FROM pain_record WHERE date_entry = :painRecordDate LIMIT 1")
    PainRecord findByDate(long painRecordDate);

    @Query("SELECT * FROM pain_record WHERE userEmail = :painRecordUserEmail LIMIT 1")
    PainRecord findByUserEmail(String painRecordUserEmail);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void updatePainRecord(PainRecord painRecord);

    @Query("DELETE FROM pain_record")
    void deleteAll();


    class PainLocationFrequency {
        public String pain_location;
        public Integer count;

        public PainLocationFrequency(String pain_location, Integer count) {
            this.pain_location = pain_location;
            this.count = count;
        }

        @Override
        public String toString() {
            return "PainLocationFrequency { " +
                    "location= " + pain_location +
                    ", count= " + count +
                    " }";
        }
    }
}
