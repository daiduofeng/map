package cn.mapdatabse;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class mapmarkViewModel extends AndroidViewModel {
    private MapMarkRepository mapMarkRepository;
    public mapmarkViewModel(@NonNull Application application) {
        super(application);
        mapMarkRepository = new MapMarkRepository(application);
    }
    //调用数据仓库获取查询值
    public LiveData<List<mapmark>> getSearchMapMarksLive(String name){
        return mapMarkRepository.getSearchMapMarksLive(name);
    }
    public List<mapmark> getAllmarksList(){
        return mapMarkRepository.getAllmarksList();
    }
    public LiveData<List<mapmark>> getAllmarks(){
        return mapMarkRepository.getAllmarks();
    }
    public LiveData<List<mapmark>> getTypeMapMarksLive(String type){
        return mapMarkRepository.getTypeMapMarksLive(type);
    }
    public void insertMarks(mapmark... mapmarks){
//        Log.d("insearchmark", "insertMarks: "+mapmarks[0].getName());
        mapMarkRepository.insertMarks(mapmarks);
    }
    public void updateMarks(mapmark... mapmarks){
        mapMarkRepository.updateMarks(mapmarks);
    }
    public void deleteMarks(mapmark... mapmarks){
        mapMarkRepository.deleteMarks(mapmarks);
    }
    public void deleteAllMarks(Void... voids){
        mapMarkRepository.deleteAllMarks();
    }
}
