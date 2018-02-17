package balint.andor.trashexplorer.Classes;

import java.util.ArrayList;

/**
 * Created by Andor on 2017.10.21..
 */

public class User {
    private int id;
    private String reports, token, name, avatar, email, date;
    private ArrayList<Integer> reportIds;
    private static volatile User user = new User();

    private User() {}

    public static User getInstance(){
        if (user == null)
            user = new User();
        return user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReports() {
        return reports;
    }

    public void setReports(String reports) {
        this.reports = reports;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getReportIds() {
        return reportIds;
    }

    public void setReportIds(ArrayList<Integer> reportid) {
        this.reportIds = reportid;
    }
}
