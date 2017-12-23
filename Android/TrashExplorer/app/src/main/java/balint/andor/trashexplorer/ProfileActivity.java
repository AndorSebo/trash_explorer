package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;


public class ProfileActivity extends AppCompatActivity {

    RequestQueue reqQueue;
    Response.Listener successResponse;
    Response.ErrorListener failedResponse;
    String token;
    AlertDialog dialog;
    Dialogs dialogs;

    void initResponses(final TextView nameTv, final TextView emailTv, final TextView dateTv, final TextView reportTv){
        successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")){
                        JSONObject jsonObject = response.getJSONObject("data");
                        ArrayList<Integer> reportIds = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("reports");
                        nameTv.setText(jsonObject.getString("name"));
                        emailTv.setText(jsonObject.getString("email"));
                        dateTv.setText(jsonObject.getJSONObject("created_at").getString("date").substring(0,10));
                        reportTv.setText(jsonObject.getString("report_number")+"\nbejelentést\ntettél eddig");
                        for (int i=0; i< jsonArray.length();i++)
                            reportIds.add(jsonArray.getJSONObject(i).getInt("report_id"));
                        Global.setReportIds(reportIds);
                        dialogs.hideAlertDialog();
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


        TextView nameTv = (TextView) findViewById(R.id.nameTv);
        TextView emailTv = (TextView) findViewById(R.id.emailTv);
        TextView dateTv = (TextView) findViewById(R.id.dateTv);
        TextView reportTv = (TextView) findViewById(R.id.report_number);
        final FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.menu);
        dialogs = new Dialogs(ProfileActivity.this);

        FloatingActionButton pwChange = (FloatingActionButton) findViewById(R.id.pwChange);
        FloatingActionButton logout = (FloatingActionButton) findViewById(R.id.logout);
        FloatingActionButton report = (FloatingActionButton) findViewById(R.id.report);
        FloatingActionButton myReports = (FloatingActionButton) findViewById(R.id.myReports);
        FloatingActionButton users = (FloatingActionButton) findViewById(R.id.users);

        User u = Global.getUser();
        int id = u.getId();
        token = u.getToken();
        int permission = u.getPermission();
        if (permission == 1){
            users.setVisibility(View.VISIBLE);
        }
        initResponses(nameTv,emailTv,dateTv,reportTv);
        Context ctx = ProfileActivity.this;
        if (Global.isNetwork(ctx))
            getProfile(id,token);
        else
            Global.networkNotFound(ctx);

        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                users(ProfileActivity.this);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverLogout();
            }
        });

        pwChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.collapse();
                pwDialog(token);
            }
        });

        myReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {myReports(ProfileActivity.this);
            }
        });

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report(ProfileActivity.this);
            }
        });

    }
    void getProfile(int id, String token){
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl()+"/profile";
        String bonus = "?token="+token+"&userid="+id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+bonus,new JSONObject(),successResponse, failedResponse);
        request.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqQueue.add(request);
        reqQueue.start();
        dialogs.showLoadingDialog();
    }
    void pwDialog(final String token){
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.password_change, null);
        final EditText oldPw = dialogLayout.findViewById(R.id.oldPw);
        final EditText newPw = dialogLayout.findViewById(R.id.newPw);
        final EditText cPw = dialogLayout.findViewById(R.id.cPw);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        dialog = builder.create();
        dialog.show();
        dialogLayout.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialogLayout.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwChange(oldPw,newPw,cPw,token,dialog);
            }
        });
        dialogLayout.findViewById(R.id.soldPassword).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, oldPw);
                return true;
            }
        });
        dialogLayout.findViewById(R.id.snewPassword).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, newPw);
                return true;
            }
        });
        dialogLayout.findViewById(R.id.scPassword).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, cPw);
                return true;
            }
        });
    }
    void pwChange(EditText oldPw, EditText newPw, EditText cPw, String token, final AlertDialog dialog){
        final String pass1 = oldPw.getText().toString();
        final String pass2 = newPw.getText().toString();
        final String pass3 = cPw.getText().toString();
        String url = Global.getBaseUrl() + "/passwordchange";
        if (pass1.equals(pass2))
            newPw.setError("A régi és az új jelszó ugyanaz");
        else if (!pass2.equals(pass3))
            cPw.setError("Nem egyezik a megerősítő jelszó, és az új jelszó.");
        else{
            dialogs.showLoadingDialog();
            StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?token="+token,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            dialog.dismiss();
                            dialogs.showSuccessDialog();
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            if(networkResponse.statusCode == 472)
                                dialogs.showErrorDialog(getString(R.string.invalid_old_password));
                            else
                                dialogs.showErrorDialog(getString(R.string.wrong));
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String>  params = new HashMap<>();
                    params.put("old",pass1);
                    params.put("new",pass2);
                    params.put("confirm_new",pass3);

                    return params;
                }
            };
            reqQueue.add(postRequest);
        }
    }
    void logout(Context ctx){
        Intent logout = new Intent(ctx,MainActivity.class);
        startActivity(logout);
        finish();
    }
    void serverLogout(){
        String url = Global.getBaseUrl()+"/logout";
        dialogs.showLoadingDialog();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url+"?token="+token,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        logout(ProfileActivity.this);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogs.showErrorDialog(getString(R.string.wrong));
                    }
                }
        );
        reqQueue.add(postRequest);
    }
    void report(Context ctx){
        Intent report = new Intent(ctx, ReportActivity.class);
        startActivity(report);
        finish();
    }
    void myReports(Context ctx){
        Intent myReports = new Intent(ctx, MyReportsActivity.class);
        startActivity(myReports);
        finish();
    }
    void users(Context ctx){
        Intent users = new Intent(ctx, UsersActivity.class);
        startActivity(users);
        finish();
    }
    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        else
            serverLogout();
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }
}
