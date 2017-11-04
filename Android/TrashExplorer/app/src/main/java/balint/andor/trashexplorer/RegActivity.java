package balint.andor.trashexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;

public class RegActivity extends AppCompatActivity {

    RequestQueue reqQueue;
    Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView login = (TextView) findViewById(R.id.backLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        final EditText pwET = (EditText) findViewById(R.id.password);
        ImageView showPW = (ImageView) findViewById(R.id.showPassword);
        final EditText cpwET = (EditText) findViewById(R.id.cpassword);
        ImageView cshowPW = (ImageView) findViewById(R.id.cshowPassword);
        ActionProcessButton regButton = (ActionProcessButton) findViewById(R.id.register);
        final EditText username = (EditText) findViewById(R.id.username);
        final EditText email = (EditText) findViewById(R.id.email);
        dialogs = new Dialogs(RegActivity.this);

        reqQueue = Volley.newRequestQueue(this);
        reqQueue.start();


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration(username, email, pwET, cpwET);
                dialogs.showLoadingDialog();
            }
        });

        showPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, pwET);
                return true;
            }
        });
        cshowPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, cpwET);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(RegActivity.this, MainActivity.class);
        startActivity(login);
        finish();
    }

    void registration(final EditText username, final EditText email, final EditText password, final EditText repassword) {
        String url = Global.getBaseUrl()+"/signup";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        dialogs.showSuccessDialog();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", error.toString());
                        dialogs.showErrorDialog(getString(R.string.wrong));
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<>();
                params.put("name",username.getText().toString());
                params.put("email",email.getText().toString());
                params.put("password",password.getText().toString());
                params.put("repassword",repassword.getText().toString());

                return params;
            }
        };
        reqQueue.add(postRequest);
    }
}
