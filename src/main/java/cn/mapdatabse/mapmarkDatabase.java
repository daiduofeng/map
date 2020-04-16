package cn.mapdatabse;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {mapmark.class},version = 1,exportSchema = false)
public abstract class mapmarkDatabase extends RoomDatabase {
    private static mapmarkDatabase INSTANCE;
    static mapmarkDatabase getDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),mapmarkDatabase.class,"word_database")

                    .build();
        }
        return INSTANCE;
    }
    public abstract mapmarkDao getmapmarkDao();
}
