package cn.mapdatabse;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface mapmarkDao {
    @Insert
    void insertMarks(mapmark... mapmarks);
    @Update
    int updateMarks(mapmark... mapmarks);
    @Delete
    void deleteMarks(mapmark... mapmarks);
    @Query("DELETE FROM MAPMARK")
    void deleteAllMarks();
    @Query("SELECT * FROM mapmark WHERE name LIKE '%' || :name || '%'")
    LiveData<List<mapmark>> getSearchMarks(String name);
    @Query("SELECT * FROM mapmark ORDER BY ID DESC")
    LiveData<List<mapmark>> getAllMarks();
    @Query("SELECT * FROM mapmark WHERE type LIKE '%' || :type || '%'")
    LiveData<List<mapmark>> getTypeMarks(String type);
    @Query("SELECT * FROM mapmark ORDER BY ID DESC")
    List<mapmark> getAllMarksList();

}
