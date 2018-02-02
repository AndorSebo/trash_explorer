package balint.andor.trashexplorer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;
import balint.andor.trashexplorer.Classes.UserAdapter;

public class UsersActivity extends AppCompatActivity {

    RequestQueue reqQueue;
    ArrayList<User> users;
    ListAdapter usersAdapter;
    ListView usersListView;
    CustomFont customFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        customFont = new CustomFont(UsersActivity.this);
        User u = Global.getUser();
        String token = u.getToken();
        usersListView = (ListView) findViewById(R.id.userListView);
        users = new ArrayList<>();
        getUsers(token, UsersActivity.this);
        Log.d("User", String.valueOf(users.size()));
    }

    void getUsers(String token, final Context ctx) {
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl() + "/getalluser";
        String t = "?token=" + token;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + t, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject jsonObject;
                                User u;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    u = new User();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    u.setName(jsonObject.getString("name"));
                                    u.setId(jsonObject.getInt("user_id"));
                                    users.add(u);
                                }
                                Collections.sort(users, new Comparator<User>() {
                                    @Override
                                    public int compare(User user, User t1) {
                                        return user.getName().compareTo(t1.getName());
                                    }
                                });
                                usersAdapter = new UserAdapter(ctx, users);
                                usersListView.setAdapter(usersAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", String.valueOf(error));
                    }
                }
        );
        reqQueue.add(getRequest);
        reqQueue.start();
    }

    @Override
    public void onBackPressed() {
        Global.openProfile(UsersActivity.this);
    }
}
