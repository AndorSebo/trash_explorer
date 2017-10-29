package balint.andor.trashexplorer;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;


public class ProfileActivity extends AppCompatActivity {

    RequestQueue reqQueue;
    Response.Listener successResponse;
    Response.ErrorListener failedResponse;

    void initResponses(final TextView nameTv, final TextView emailTv, final TextView dateTv, final TextView reportTv, final LinearLayout reports, final TextView report_zero){
        successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")){
                        JSONObject jsonObject = response.getJSONObject("data");
                        nameTv.setText(jsonObject.getString("name"));
                        emailTv.setText(jsonObject.getString("email"));
                        dateTv.setText(jsonObject.getJSONObject("created_at").getString("date").substring(0,10));
                        reportTv.setText(jsonObject.getString("report_number")+"\n bejelentés");
                        if (!jsonObject.getString("report_number").equals("0")){
                            reports.setVisibility(View.VISIBLE);
                            report_zero.setVisibility(View.GONE);
                        }
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
        TextView report_zero = (TextView) findViewById(R.id.report_zero);
        ImageView backButton = (ImageView) findViewById(R.id.backButton);
        LinearLayout reports = (LinearLayout) findViewById(R.id.reports);
        ImageView pwButton = (ImageView) findViewById(R.id.pwButton);

        User u = Global.getUser();
        int id = u.getId();
        String token = u.getToken();
        initResponses(nameTv,emailTv,dateTv,reportTv, reports, report_zero);
        getProfile(id,token);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        pwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pwDialog();
            }
        });
    }
    void getProfile(int id, String token){
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl()+"/profile";
        String bonus = "?token="+token+"&userid="+id;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url+bonus,new JSONObject(),successResponse, failedResponse);
        reqQueue.add(request);
        reqQueue.start();
    }

    void pwDialog(){
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.password_change, null);
        final EditText oldPw = dialogLayout.findViewById(R.id.oldPw);
        final EditText newPw = dialogLayout.findViewById(R.id.newPw);
        final EditText cPw = dialogLayout.findViewById(R.id.cPw);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogLayout);
        final AlertDialog dialog = builder.create();
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
                pwChange(oldPw,newPw,cPw);
            }
        });
    }

    void pwChange(EditText oldPw, EditText newPw, EditText cPw){
        
    }

    @Override
    public void onBackPressed() {
        Intent menu = new Intent(ProfileActivity.this, MenuActivity.class);
        startActivity(menu);
        finish();
    }
    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }
}
