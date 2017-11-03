package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import balint.andor.trashexplorer.Classes.GPStracker;
import balint.andor.trashexplorer.Classes.Report;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    MapFragment mapFragment;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        ImageView[] imgs = new ImageView[4];

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
                send();
            }
        });
    }

    void makePicture(ImageView img){
        openIntent();
    }
    void openIntent(){
        
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
    void send(){
        Report report = new Report();

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
}
