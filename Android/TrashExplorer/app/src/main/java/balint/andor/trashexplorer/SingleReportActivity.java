package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;

import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;
import balint.andor.trashexplorer.Classes.UserAdapter;

public class SingleReportActivity extends AppCompatActivity implements OnMapReadyCallback {


    MapFragment mapFragment;
    RequestQueue reqQueue;
    User u;
    double latitude, longitude;
    Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        TextView gpsNeed = (TextView) findViewById(R.id.gpsNeed);
        View blackMask = findViewById(R.id.blackMask);
        TextView coordNotFound = (TextView) findViewById(R.id.coordNotFound);
        EditText description = (EditText) findViewById(R.id.description);
        dialogs = new Dialogs(SingleReportActivity.this);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        u = Global.getUser();

        getReport(blackMask,coordNotFound,String.valueOf(getIntent().getExtras().getInt("id")),description);
        send.setVisibility(View.GONE);
        locate.setVisibility(View.GONE);
        gpsNeed.setVisibility(View.GONE);
    }

    void getReport(final View blackMask, final TextView coordNotFound, String reportid, final EditText description){
        dialogs.showLoadingDialog();
        description.setEnabled(false);
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl() + "/getreport";
        String token = "?token="+u.getToken();
        reportid ="&reportid="+reportid;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url+token+reportid, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getBoolean("success")){
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                latitude = jsonObject.getDouble("latitude");
                                longitude = jsonObject.getDouble("longitude");
                                hideMap(blackMask,coordNotFound,latitude,longitude);
                                description.setText(jsonObject.getString("description"));
                                dialogs.hideAlertDialog();

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
                        Log.d("Error.Response", String.valueOf(error));
                        dialogs.showErrorDialog(getResources().getString(R.string.wrong));
                    }
                }
        );
        reqQueue.add(getRequest);
        reqQueue.start();
    }

    void hideMap(View blackMask, TextView coordNotFound, double latitude, double longitude){
        if (latitude == 1 && longitude == 1){
            blackMask.setVisibility(View.VISIBLE);
            coordNotFound.setVisibility(View.VISIBLE);
        }else{
            mapFragment.getMapAsync(SingleReportActivity.this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title("You are here!"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    void openMyReports(Context ctx){
        Intent myReports = new Intent(ctx, MyReportsActivity.class);
        startActivity(myReports);
        finish();
    }

    @Override
    public void onBackPressed() {
        openMyReports(SingleReportActivity.this);
    }
}
