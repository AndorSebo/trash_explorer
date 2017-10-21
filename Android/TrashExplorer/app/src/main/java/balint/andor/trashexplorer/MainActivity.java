package balint.andor.trashexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.Global;

public class MainActivity extends AppCompatActivity {

    RequestQueue reqQueue;
    Response.Listener successResponse;
    Response.ErrorListener failedResponse;

    void initResponses(){
        successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")){
                        Global.setId(response.getInt("userid"));
                        Global.setToken(response.getJSONObject("user").getString("token"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        failedResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Global.networkNotFound(MainActivity.this);
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView eye = (ImageView) findViewById(R.id.showPassword);
        TextView signUp = (TextView) findViewById(R.id.signUp);
        ActionProcessButton signInButton = (ActionProcessButton) findViewById(R.id.signIn);
        final EditText pwET = (EditText) findViewById(R.id.password);
        final EditText emailET = (EditText) findViewById(R.id.email);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistration();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetwork(MainActivity.this))
                    logIn(emailET,pwET);
                else
                    Global.networkNotFound(MainActivity.this);
            }
        });
        eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Global.showPassword(event, pwET);
                return true;
            }
        });
        initResponses();
    }

    private void openRegistration() {
        Intent registration = new Intent(MainActivity.this, RegActivity.class);
        startActivity(registration);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }

    void logIn(EditText emailET, EditText pwET){
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl()+"/signin";
        Map<String, String> params = new HashMap<>();
        if(emailET.getText().toString().equals(""))
            emailET.setError("Email field is empty!");
        else if (pwET.getText().toString().equals(""))
            pwET.setError("Password filed is empty!");
        else{
            params.put("email",emailET.getText().toString());
            params.put("password",pwET.getText().toString());
            JSONObject js = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                    successResponse, failedResponse);
            reqQueue.add(request);
            reqQueue.start();
        }
    }


}
