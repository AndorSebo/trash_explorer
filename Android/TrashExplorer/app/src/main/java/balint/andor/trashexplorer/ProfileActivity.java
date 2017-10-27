package balint.andor.trashexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ihsanbal.wiv.MediaView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.User;


public class ProfileActivity extends AppCompatActivity {

    RequestQueue reqQueue;

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

        reqQueue = Volley.newRequestQueue(this);
        reqQueue.start();

        User u = Global.getUser();
        int id = u.getId();
        String token = u.getToken();
        //getProfile(id,token, nameTv, emailTv, dateTv, reportTv);

    }
}
