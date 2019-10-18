package ua.esputnik.support.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "push")
public class PushEntity {

    @PrimaryKey
    @NonNull
    public String iid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

//    @ColumnInfo(name = "image")
//    public Bitmap image;


}
