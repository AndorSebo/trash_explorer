package balint.andor.trashexplorer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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

import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.Report;
import balint.andor.trashexplorer.Classes.ReportAdapter;
import balint.andor.trashexplorer.Classes.User;

public class MyReportsActivity extends AppCompatActivity {

     static RequestQueue queue;
     User u;
     ListAdapter reportsAdapter;
     ListView reportList;
     ArrayList<Report> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reports);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        descriptions = new ArrayList<>();
        queue = Volley.newRequestQueue(this);
        u = Global.getUser();
        reportList = (ListView) findViewById(R.id.myReports);
        getReports(this, u.getId());
        reportList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MyReportsActivity.this, String.valueOf(descriptions.get(i).getReport_id()), Toast.LENGTH_SHORT).show();
            }
        });
    }
    void getReports(final Context ctx, int userid){
        String url = Global.getBaseUrl() + "/getuserreports";
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+"?token="+u.getToken()+"&userid="+userid, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")){
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject jsonObject;
                                Report r;
                                for (int i=0; i< jsonArray.length(); i++){
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
                                reportsAdapter = new ReportAdapter(ctx,descriptions);
                                reportList.setAdapter(reportsAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );
        queue.add(getRequest);
    }

    @Override
    public void onBackPressed() {
        Global.openProfile(MyReportsActivity.this);
    }
}
