package balint.andor.trashexplorer.Classes;

import java.util.ArrayList;

/**
 * Created by Andor on 2017.10.21..
 */

public class User {
    private int id, permission;
    private String token, name, avatar;
    private ArrayList<Integer> reportIds;
    private static volatile User user = new User();

    private User() {}

    public static User getInstance(){
        if (user == null)
            user = new User();
        return user;
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

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
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
