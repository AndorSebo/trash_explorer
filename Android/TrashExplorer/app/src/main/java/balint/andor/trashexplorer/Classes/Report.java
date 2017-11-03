package balint.andor.trashexplorer.Classes;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Andor on 2017.11.03..
 */

public class Report {
    private double latitude, longitude;
    private String description;
    private ArrayList<Bitmap> images;
    private int picnumber;


    public Report() {}

    public int getPicnumber() {
        return picnumber;
    }

    public void setPicnumber(int picnumber) {
        this.picnumber = picnumber;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Bitmap> getImages() {
        return images;
    }

    public void setImages(ArrayList<Bitmap> images) {
        this.images = images;
    }
}
