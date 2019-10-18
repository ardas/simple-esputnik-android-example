package ua.esputnik.support.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PushEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PushDao pushDao();
}
