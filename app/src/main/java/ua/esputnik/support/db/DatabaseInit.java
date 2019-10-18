package ua.esputnik.support.db;

import android.content.Context;

import androidx.room.Room;

public class DatabaseInit {

    private static volatile AppDatabase appDb;

    public static AppDatabase getDB(Context ctx) {
        if (null == appDb) {
            synchronized (DatabaseInit.class) {
                appDb = Room.databaseBuilder(ctx.getApplicationContext(),
                        AppDatabase.class, "es-push-db").build();
            }
        }
        return appDb;
    }


}
