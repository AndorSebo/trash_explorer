package balint.andor.trashexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.MenuItems;
import balint.andor.trashexplorer.Classes.User;


public class ProfileActivity extends AppCompatActivity {

    ListView mDrawerList;
    private RequestQueue reqQueue;
    private Response.Listener successResponse;
    private Response.ErrorListener failedResponse;
    private String token;
    private AlertDialog dialog;
    private Dialogs dialogs;
    private ArrayAdapter<String> mAdapter;
    private CustomFont customFont;
    private ImageButton menuButton;
    private DrawerLayout mDrawerLayout;
    private User user;
    private ArrayList<Activity> activities;
    private ImageView avatar;
    private TextView nameTv, emailTv, dateTv, reportTv;


    void initResponses() {
        successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        ArrayList<Integer> reportIds = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("reports");
                        String regDateString = jsonObject.getJSONObject("created_at").getString("date").substring(0, 10);
                        nameTv.setText(jsonObject.getString("name"));
                        emailTv.setText(jsonObject.getString("email"));
                        dateTv.setText(regDate(regDateString));
                        reportTv.setText(jsonObject.getString("report_number"));
                        for (int i = 0; i < jsonArray.length(); i++)
                            reportIds.add(jsonArray.getJSONObject(i).getInt("report_id"));
                        user.setReportIds(reportIds);
                        if (!"images/avatar/default.png".equals(user.getAvatar())){
                            Picasso.with(ProfileActivity.this).load(Global.getBaseUrl()+"/"+user.getAvatar())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .into(avatar);
                        }
                        Dialogs.hideAlertDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        failedResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.networkNotFound(ProfileActivity.this);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        customFont = new CustomFont(ProfileActivity.this);
        nameTv = (TextView) findViewById(R.id.nameTv);
        emailTv = (TextView) findViewById(R.id.emailTv);
        dateTv = (TextView) findViewById(R.id.dateTv);
        reportTv = (TextView) findViewById(R.id.report_number);
        mDrawerList = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        MenuItems menuItems = new MenuItems(ProfileActivity.this);
        dialogs = Dialogs.getInstance();
        avatar = (ImageView) findViewById(R.id.avatar);
        user = User.getInstance();
        int id = user.getId();
        token = user.getToken();
        initResponses();
        Context ctx = ProfileActivity.this;

        if (Global.isNetwork(ctx))
            getProfile(id, token);
        else
            Global.networkNotFound(ctx);

        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems.getItems());
        mDrawerList.setAdapter(mAdapter);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.END);
            }
        });
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Global.menu(i,ProfileActivity.this);
            }
        });
        //mDrawerList.addHeaderView(getLayoutInflater().inflate(R.layout.menu_header,null, false));
    }

    private void getProfile(int id, String token) {
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl() + "/profile";
        String bonus = "?token=" + token + "&userid=" + id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url + bonus, new JSONObject(), successResponse, failedResponse);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqQueue.add(request);
        reqQueue.start();
        Dialogs.showLoadingDialog(ProfileActivity.this).show();
    }

    private String regDate(String s) {
        char[] c = s.toCharArray();
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '-')
                c[i] = '.';
            sBuilder.append(c[i]);
        }
        s = sBuilder.toString();
        return s;
    }
    @Override
    public void onBackPressed() {
            Global.logout(ProfileActivity.this);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }
}
