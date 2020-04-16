package cn.mapdatabse;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Update;

import java.util.List;

public class MapMarkRepository {
    private LiveData<List<mapmark>>allmarks;
    private List<mapmark>allmarksList;

    private LiveData<List<mapmark>> searchMapMarksLive;
    private mapmarkDao mapMarkDao;
    public MapMarkRepository(Context context) {
        mapmarkDatabase mapMarkDatabase = mapmarkDatabase.getDatabase(context.getApplicationContext());
        mapMarkDao = mapMarkDatabase.getmapmarkDao();
        allmarks = mapMarkDao.getAllMarks();
        allmarksList = mapMarkDao.getAllMarksList();

    }
    public List<mapmark> getAllmarksList(){
        return allmarksList;
    }
    public LiveData<List<mapmark>> getAllmarks(){
        return allmarks;
    }
    public LiveData<List<mapmark>> getSearchMapMarksLive(String name){
        searchMapMarksLive = mapMarkDao.getSearchMarks(name);
        return searchMapMarksLive;
    }
    public LiveData<List<mapmark>> getTypeMapMarksLive(String type){
        searchMapMarksLive = mapMarkDao.getTypeMarks(type);
        return searchMapMarksLive;
    }

    void insertMarks(mapmark... mapmarks){
        new InsertAsyncTask(mapMarkDao).execute(mapmarks);
    }
    void updateMarks(mapmark... mapmarks){
        new UpdateAsyncTask(mapMarkDao).execute(mapmarks);
    }
    void deleteMarks(mapmark... mapmarks){
        new DeleteAsyncTask(mapMarkDao).execute(mapmarks);
    }
    void deleteAllMarks(Void... voids){
        new DeleteAllAsyncTask(mapMarkDao).execute();
    }
    static class InsertAsyncTask extends AsyncTask<mapmark,Void,Void>{

        private mapmarkDao mapMarkDao;
        public InsertAsyncTask(mapmarkDao mapMarkDao) {
            this.mapMarkDao = mapMarkDao;
        }

        @Override
        protected Void doInBackground(mapmark... mapmarks) {
            Log.d("insearchmark", "insertMarks: "+mapmarks[0].getName());
            mapMarkDao.insertMarks(mapmarks);
            return null;
        }
    }
    static class UpdateAsyncTask extends AsyncTask<mapmark,Void,Void>{

        private mapmarkDao mapMarkDao;
        public UpdateAsyncTask(mapmarkDao mapMarkDao) {
            this.mapMarkDao = mapMarkDao;
        }

        @Override
        protected Void doInBackground(mapmark... mapmarks) {
            mapMarkDao.updateMarks(mapmarks);
            return null;
        }
    }
    static class DeleteAsyncTask extends AsyncTask<mapmark,Void,Void>{

        private mapmarkDao mapMarkDao;
        public DeleteAsyncTask(mapmarkDao mapMarkDao) {
            this.mapMarkDao = mapMarkDao;
        }

        @Override
        protected Void doInBackground(mapmark... mapmarks) {
            mapMarkDao.deleteMarks(mapmarks);
            return null;
        }
    }
    static class DeleteAllAsyncTask extends AsyncTask<Void,Void,Void>{

        private mapmarkDao mapMarkDao;
        public DeleteAllAsyncTask(mapmarkDao mapMarkDao) {
            this.mapMarkDao = mapMarkDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mapMarkDao.deleteAllMarks();
            return null;
        }
    }
//    static class SearchAsyncTask extends AsyncTask<String,Void,Void>{
//
//        private mapmarkDao mapMarkDao;
//        public SearchAsyncTask(mapmarkDao mapMarkDao) {
//            this.mapMarkDao = mapMarkDao;
//        }
//
//        @Override
//        protected Void doInBackground(String name) {
//            mapMarkDao.getSearchMark(name);
//            return null;
//        }
//    }


}
