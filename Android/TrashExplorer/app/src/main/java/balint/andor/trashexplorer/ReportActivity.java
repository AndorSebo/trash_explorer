package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mvc.imagepicker.ImagePicker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.GPStracker;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.Report;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    double latitude = 1.00, longitude = 1.00;
    ImageView selectedImageView;
    EditText description;
    RequestQueue reqQueue;
    Dialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        final ImageView[] imgs = new ImageView[4];
        description = (EditText) findViewById(R.id.description);
        dialogs = new Dialogs(ReportActivity.this);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        declarateImgs(imgs);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locateMe(ReportActivity.this);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Report report = send(imgs);
                if (!report.getDescription().equals("")){
                    sendData(report);
                }
            }
        });
        reqQueue = Volley.newRequestQueue(this);
        reqQueue.start();
    }

    void makePicture(ImageView img){
        ImagePicker.pickImage(ReportActivity.this,getResources().getString(R.string.selectMode));
        selectedImageView = img;

    }
    void declarateImgs(final ImageView[] imgs){
        for (int i=0; i< imgs.length;i++) {
            int resID = getResources().getIdentifier(("img" + i), "id", getPackageName());
            imgs[i] = (ImageView) findViewById(resID);
            final int j = i;
            imgs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    makePicture(imgs[j]);
                }
            });
        }
    }
    Report send(ImageView[] imgs){
        Report report = new Report();
        ArrayList<ImageView> list = new ArrayList<>(Arrays.asList(imgs));
        for (Iterator<ImageView> iterator = list.iterator(); iterator.hasNext();){
            ImageView selected = iterator.next();
            if (selected.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.image_add).getConstantState())){
                iterator.remove();
            }

        }

        if (!description.getText().toString().equals("")){
            report.setDescription(description.getText().toString());
            report.setImages(list);
            report.setLatitude(latitude);
            report.setLongitude(longitude);
            dialogs.showLoadingDialog();
        }else{
            description.setError("Kötelező leírást megadni!");
        }
        return report;
    }
    void openProfile(Context ctx){
        Intent profile = new Intent(ctx, ProfileActivity.class);
        startActivity(profile);
        finish();
    }
    void locateMe(Context ctx){
        Location location;
        GPStracker gps = new GPStracker(ctx);
        location = gps.getLocation();
        if (location==null){
            Toast.makeText(ctx, getResources().getString(R.string.check_the_gps_connection), Toast.LENGTH_SHORT).show();
        }else{
            mapFragment.getMapAsync(ReportActivity.this);
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        }
    }
    void setLatitude(double latitude){ this.latitude = latitude; }
    void setLongitude(double longitude){ this.longitude = longitude; }
    double getLatitude(){ return latitude;}
    double getLongitude(){ return longitude;}
    @Override
    public void onBackPressed() {
        openProfile(ReportActivity.this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.clear();
        LatLng location = new LatLng(getLatitude(), getLongitude());
        googleMap.addMarker(new MarkerOptions().position(location).title("You are here!"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        if (bitmap != null){
            selectedImageView.setImageBitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void sendData(final Report report){
        String url = Global.getBaseUrl()+"/newreport";
        String token = Global.getUser().getToken();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?token="+token,
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
                int picnumber = report.getImages().size();
                Map<String, String>  params = new HashMap<>();
                params.put("latitude",String.valueOf(report.getLatitude()));
                params.put("longitude",String.valueOf(report.getLongitude()));
                params.put("description",report.getDescription());
                params.put("picnumber",String.valueOf(picnumber));
                for(int i=0; i< picnumber; i++){
                    params.put("picture"+(i+1), convertToBase64(((BitmapDrawable)report.getImages().get(i).getDrawable()).getBitmap()));
                }
                Log.d("Params",params.toString());
                return params;
            }
        };
        reqQueue.add(postRequest);
    }
    String convertToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray =  byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
