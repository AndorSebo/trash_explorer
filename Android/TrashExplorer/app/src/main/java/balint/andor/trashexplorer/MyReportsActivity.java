package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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
import balint.andor.trashexplorer.Classes.MenuHeader;
import balint.andor.trashexplorer.Classes.MenuItems;
import balint.andor.trashexplorer.Classes.Report;
import balint.andor.trashexplorer.Classes.ReportAdapter;
import balint.andor.trashexplorer.Classes.User;

public class MyReportsActivity extends AppCompatActivity {

    private static RequestQueue queue;
    private User u;
    private ListAdapter reportsAdapter;
    private ListView reportList;
    private ArrayList<Report> descriptions;
    private CustomFont customFont;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
       CustomFont.getInstance().init(MyReportsActivity.this);
        descriptions = new ArrayList<>();
        mDrawerList = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        MenuItems menuItems = new MenuItems(MyReportsActivity.this);
        queue = Volley.newRequestQueue(this);
        u = User.getInstance();
        reportList = (ListView) findViewById(R.id.myReports);
        Context ctx = MyReportsActivity.this;
        if (Global.isNetwork(ctx))
            getReports(this, u.getId());
        else
            Global.networkNotFound(ctx);
        reportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openReport(MyReportsActivity.this, descriptions.get(i).getReport_id());

            }
        });
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
                Global.menu(i,MyReportsActivity.this);
            }
        });
        mDrawerList.addHeaderView(MenuHeader.getInstance().init(MyReportsActivity.this));
    }

    void getReports(final Context ctx, int userid) {
        String url = Global.getBaseUrl() + "/getuserreports";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + "?token=" + u.getToken() + "&userid=" + userid, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject jsonObject;
                                Report r;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jsonObject = jsonArray.getJSONObject(i);
                                    r = new Report();
                                    r.setDescription(jsonObject.getString("description"));
                                    r.setReport_id(jsonObject.getInt("report_id"));
                                    descriptions.add(r);
                                }
                                Collections.sort(descriptions, new Comparator<Report>() {
                                    @Override
                                    public int compare(Report report, Report t1) {
                                        return report.getDescription().compareTo(t1.getDescription());
                                    }
                                });
                                reportsAdapter = new ReportAdapter(ctx, descriptions);
                                reportList.setAdapter(reportsAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(getRequest);
    }

    void openReport(Context ctx, int id) {
        Intent report = new Intent(ctx, SingleReportActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        report.putExtras(bundle);
        startActivity(report);
        finish();
    }

    @Override
    public void onBackPressed() {
        Global.openProfile(MyReportsActivity.this);
    }
}
