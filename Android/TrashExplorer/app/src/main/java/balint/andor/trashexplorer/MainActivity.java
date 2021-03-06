package balint.andor.trashexplorer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;

public class MainActivity extends AppCompatActivity {

    private RequestQueue reqQueue;
    private Response.Listener successResponse;
    private Response.ErrorListener failedResponse;
    private CheckBox dataCB;
    private EditText pwET;
    private EditText emailET;
    private SharedPreferences sharedPref;
    private FirebaseAnalytics mFirebaseAnalytics;
    private User user;

    void initResponses() {
        successResponse = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        if (dataCB.isChecked()) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("email", emailET.getText().toString());
                            editor.putString("password", pwET.getText().toString());
                            editor.apply();
                        }
                        user.setId(response.getInt("userid"));
                        user.setToken(response.getJSONArray("user").getJSONObject(0).getString("token"));
                        user.setAvatar(response.getJSONArray("user").getJSONObject(1).getString("avatar"));
                        logToFireBase(emailET.getText().toString());
                        Global.openProfile(MainActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        failedResponse = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Dialogs.showErrorDialog(getString(R.string.invalidEmailPass), MainActivity.this).show();
            }
        };
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CustomFont.getInstance().init(MainActivity.this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        user = User.getInstance();
        getCameraPermission();
        ImageView eye = (ImageView) findViewById(R.id.showPassword);
        TextView signUp = (TextView) findViewById(R.id.signUp);
        ActionProcessButton signInButton = (ActionProcessButton) findViewById(R.id.signIn);
        final Context ctx = MainActivity.this;
        pwET = (EditText) findViewById(R.id.password);
        emailET = (EditText) findViewById(R.id.email);
        dataCB = (CheckBox) findViewById(R.id.dataCB);
        sharedPref = ((Activity) ctx).getPreferences(Context.MODE_PRIVATE);
        emailET.setText(sharedPref.getString("email", ""));
        pwET.setText(sharedPref.getString("password", ""));
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegistration();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetwork(ctx))
                    logIn(emailET, pwET);
                else
                    Global.networkNotFound(ctx);
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

    private void getCameraPermission(){
        if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    android.Manifest.permission.CAMERA}, 0);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }

    void logIn(EditText emailET, EditText pwET) {
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl() + "/signin";
        Map<String, String> params = new HashMap<>();
        if ("".equals(emailET.getText().toString()))
           Dialogs.showErrorDialog(getString(R.string.empty_email),MainActivity.this);
        else if ("".equals(pwET.getText().toString()))
            Dialogs.showErrorDialog(getString(R.string.empty_password),MainActivity.this);
        else {
            params.put("email", emailET.getText().toString());
            params.put("password", pwET.getText().toString());
            JSONObject js = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js,
                    successResponse, failedResponse);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            reqQueue.add(request);
            reqQueue.start();
            Dialogs.showLoadingDialog(MainActivity.this).show();
        }
    }

    void logToFireBase(String email) {
        Bundle bundle = new Bundle();
        bundle.putString("Email", email);
        mFirebaseAnalytics.logEvent("Event", bundle);
    }

}
