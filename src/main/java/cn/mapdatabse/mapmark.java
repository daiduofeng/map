package cn.mapdatabse;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class mapmark implements Serializable {
    @PrimaryKey(autoGenerate = true)//主键，自动生成
    private int id;
    @ColumnInfo(name = "纬度")
    private double latitude;//纬度
    @ColumnInfo(name = "经度")
    private double longitude;//经度
    @ColumnInfo(name = "name")
    private String name;//名字

    @ColumnInfo(name = "imgId")
    private int imgId;//图片
    @ColumnInfo(name = "type")
    private String type;//类别
    @ColumnInfo(name = "description")
    private String description;//描述

    public mapmark(double latitude, double longitude, String name, int imgId,String type ,String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.imgId = imgId;
        this.type = type;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }

    public int getImgId() {
        return imgId;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
