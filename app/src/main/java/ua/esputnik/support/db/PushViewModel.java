package ua.esputnik.support.db;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class PushViewModel extends AndroidViewModel {

    private final AppDatabase db;
    private LiveData<List<PushEntity>> pushEntities;

    public PushViewModel(@NonNull Application application) {
        super(application);
        db = DatabaseInit.getDB(application);

    }

    public LiveData<List<PushEntity>> getAllPushes() {
        if (null == pushEntities) {
            pushEntities = new MutableLiveData<>();
            pushEntities = db.pushDao().getAll();


        }
        return pushEntities;
    }

    public void removeAll() {
        db.pushDao().deleteAll();
    }


}
