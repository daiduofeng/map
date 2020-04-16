package com.example.mapdemo;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import cn.mapdatabse.mapmark;
import cn.mapdatabse.mapmarkViewModel;


public class MapMarkDate {
    //mark数据model
    private mapmarkViewModel mapMarkViewModel;

    public MapMarkDate(mapmarkViewModel mapMarkViewModel) {
        this.mapMarkViewModel = mapMarkViewModel;
    }
    public void insertMarks(){
        mapmark[] mapmarkArray = new mapmark[5];
        mapmark mapmark1 = new mapmark(38.018725,112.452061,"铁路主题公园",R.drawable.spot_train,"景点","2018年4月，中北大学铁路主题公园景观开始建设。4个月之后，中北大学铁路主题公园终于基本建好！中北大学是全国唯一一所学校里有火车站的大学，虽然通往中北的小火车已退出历史舞台，但火车道却一直通行着。从中北大学正门（南门）进入，沿右手边大路直走，快到二道门时。就能看到一条悠长的铁道。尽头处还停放着两列废弃的火车，一列锈迹斑斑红白相间的双层硬座车，另一辆是普通绿皮小火车。公园内，铁道旁修建的木站台。");
        mapmark mapmark2 = new mapmark(38.021138,112.455709,"中北大学主楼(十一号楼)",R.drawable.teach_11,"教学楼","暂无简介");
        mapmark mapmark3 = new mapmark(38.020959,112.44833,"文瀛菀1号公寓",R.drawable.wenying_1,"宿舍","暂无简介");
        mapmark mapmark4 = new mapmark(38.019571,112.450559,"文瀛菀13号公寓",R.drawable.wenying_13,"宿舍","暂无简介");
        mapmark mapmark5 = new mapmark(38.019371,112.447387,"柏林园",R.drawable.bolinyuan,"景点","柏林园位于中北大学校内，是太原市七所四星级公园之一 [1]  ，西倚二龙山，南临汾河水，毗邻太原名胜窦大夫祠，青山碧水，风景旖旎。附近有二龙山，千佛洞、中华傅山园、窦大夫祠、多福寺等人文、自然景观。");
        mapmarkArray[0] = mapmark1;
        mapmarkArray[1] = mapmark2;
        mapmarkArray[2] = mapmark3;
        mapmarkArray[3] = mapmark4;
        mapmarkArray[4] = mapmark5;
        for (int i =0;i<mapmarkArray.length;i++){
            mapMarkViewModel.insertMarks(mapmarkArray[i]);
        }
        Log.d("insert", "insertMarks: "+mapmark1.getName());
    }
    public LiveData<List<mapmark>> getSearchMapMarksLive(String name){
        return mapMarkViewModel.getSearchMapMarksLive(name);
    }
    public void deleteAllMarks(){
        mapMarkViewModel.deleteAllMarks();
    }
}
