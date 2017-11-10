package balint.andor.trashexplorer.Classes;

import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Andor on 2017.11.03..
 */

public class Report {
    private int report_id, image_id;
    private double latitude, longitude;
    private String description;
    private ArrayList<ImageView> images;
    private ArrayList<String> mini_url;

    public int getReport_id() {
        return report_id;
    }

    public ArrayList<String> getMini_url() {
        return mini_url;
    }

    public void setMini_url(ArrayList<String> mini_url) {
        this.mini_url = mini_url;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }

    public Report() {}

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

    public ArrayList<ImageView> getImages() {
        return images;
    }

    public void setImages(ArrayList<ImageView> images) {
        this.images = images;
    }
}
