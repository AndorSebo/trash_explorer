package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import java.util.ArrayList;

import balint.andor.trashexplorer.MyReportsActivity;
import balint.andor.trashexplorer.ProfileActivity;
import balint.andor.trashexplorer.R;
import balint.andor.trashexplorer.ReportActivity;

/**
 * Created by Andor on 2018.02.03..
 */

public class MenuItems {

    private ArrayList<Activity> activities;
    private Activity a;
    private User u;
    private ArrayList<String> Array;
    public MenuItems(Activity activtiy) {
        a = activtiy;
        u = User.getInstance();
        addDrawerItems();
    }
    private void addDrawerItems() {
        Array = new ArrayList<>();
        activities = new ArrayList<>();
        activities.add(new ProfileActivity());
        activities.add(new ReportActivity());
        activities.add(new MyReportsActivity());
        Array.add(a.getResources().getString(R.string.profile));
        Array.add(a.getResources().getString(R.string.report));
        Array.add(a.getResources().getString(R.string.my_reports));
        Array.add(a.getResources().getString(R.string.logout));
    }
    public ArrayList<String> getItems(){
        return Array;
    }
    public ArrayList<Activity> getActivities(){return activities;}
}
