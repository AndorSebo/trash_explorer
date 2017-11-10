package balint.andor.trashexplorer.Classes;

import java.util.ArrayList;

/**
 * Created by Andor on 2017.10.21..
 */

public class User {
    int id, permission;
    String token;
    ArrayList<Integer> reportid;

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public ArrayList<Integer> getReportid() {
        return reportid;
    }

    public void setReportid(ArrayList<Integer> reportid) {
        this.reportid = reportid;
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
}
