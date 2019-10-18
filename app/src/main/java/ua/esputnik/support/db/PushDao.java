package ua.esputnik.support.db;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PushDao {

    @Query("SELECT * FROM push")
    LiveData<List<PushEntity>> getAll();

    @Query("SELECT count(*) FROM push")
    int count();

    @Insert
    void insertAll(PushEntity... users);

    @Query("DELETE FROM push")
    void deleteAll();

}
