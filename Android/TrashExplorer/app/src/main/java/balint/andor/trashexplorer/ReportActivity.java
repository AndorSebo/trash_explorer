package balint.andor.trashexplorer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.GPStracker;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.MenuHeader;
import balint.andor.trashexplorer.Classes.MenuItems;
import balint.andor.trashexplorer.Classes.Report;
import balint.andor.trashexplorer.Classes.User;

public class ReportActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private double latitude = 1.00, longitude = 1.00;
    private ImageView selectedImageView;
    private EditText description;
    private RequestQueue reqQueue;
    private Dialogs dialogs;
    private CustomFont customFont;
    private User user;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ImageButton menuButton;
    private int requestCode = 0, PICK_IMAGE = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = ReportActivity.this;
        CustomFont.getInstance().init(context);
        ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CAMERA}, 2);
        mDrawerList = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        MenuItems menuItems = new MenuItems((Activity)context);
        user = User.getInstance();
        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        final ImageView[] imgs = new ImageView[4];
        description = (EditText) findViewById(R.id.description);
        dialogs = Dialogs.getInstance();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        declarateImgs(imgs);

        locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locateMe(context);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetwork(context)) {
                    Report report = send(imgs);
                    if (report != null)
                        sendData(report);
                } else
                    Global.networkNotFound(context);
            }
        });
        reqQueue = Volley.newRequestQueue(this);
        reqQueue.start();
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menuItems.getItems());
        mDrawerList.setAdapter(mAdapter);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.openDrawer(Gravity.END);
            }
        });
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Global.menu(i,context);
            }
        });
        mDrawerList.addHeaderView(MenuHeader.getInstance().init(context));
    }

    void makePicture(ImageView img) {
        selectedImageView = img;
        final Dialog dialog = Dialogs.showImageDialog(context);
        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{
                            android.Manifest.permission.CAMERA}, 0);
                }
                Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(photoCaptureIntent, requestCode);
                dialog.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.changeAvatar)), PICK_IMAGE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    void declarateImgs(final ImageView[] imgs) {
        for (int i = 0; i < imgs.length; i++) {
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

    Report send(ImageView[] imgs) {
        Report report = new Report();
        ArrayList<ImageView> list = new ArrayList<>(Arrays.asList(imgs));
        for (Iterator<ImageView> iterator = list.iterator(); iterator.hasNext(); ) {
            ImageView selected = iterator.next();
            if (selected.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.image_add).getConstantState())) {
                iterator.remove();
            }

        }

        if (description.getText().toString().equals("") && latitude == 1 && longitude == 1)
            Dialogs.showErrorDialog(getResources().getString(R.string.empty_desc_and_coord), getBaseContext());
        else {
            if (description.getText().toString().equals(""))
                report.setDescription("Nem érkezett leírás");
            else
                report.setDescription(description.getText().toString());
            report.setImages(list);
            report.setLatitude(latitude);
            report.setLongitude(longitude);
            Dialogs.showLoadingDialog(context).show();
            return report;
        }
        return null;
    }

    void locateMe(Context ctx) {
        Location location;
        GPStracker gps = new GPStracker(ctx);
        location = gps.getLocation();
        if (location == null) {
            Toast.makeText(ctx, getResources().getString(R.string.check_the_gps_connection), Toast.LENGTH_SHORT).show();
        } else {
            mapFragment.getMapAsync(ReportActivity.this);
            setLatitude(location.getLatitude());
            setLongitude(location.getLongitude());
        }
    }

    double getLatitude() {
        return latitude;
    }

    void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    double getLongitude() {
        return longitude;
    }

    void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onBackPressed() {
        Global.openProfile(context);
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
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            selectedImageView.setImageBitmap(bitmap);
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageURI = data.getData();
                Picasso.with(context).load(selectedImageURI).into(selectedImageView);
            }
        }
    }

    void sendData(final Report report) {
        String url = Global.getBaseUrl() + "/newreport";
        String token = user.getToken();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url + "?token=" + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Dialog dialog = Dialogs.showSuccessDialog(getResources().getString(R.string.success_report),context);
                        ActionProcessButton ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myreports = new Intent(context,MyReportsActivity.class);
                                startActivity(myreports);
                                finish();
                            }
                        });
                        dialog.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialogs.showErrorDialog(getString(R.string.wrong), getBaseContext());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                int picnumber = report.getImages().size();
                Map<String, String> params = new HashMap<>();
                params.put("latitude", String.valueOf(report.getLatitude()));
                params.put("longitude", String.valueOf(report.getLongitude()));
                params.put("description", report.getDescription());
                params.put("picnumber", String.valueOf(picnumber));
                for (int i = 0; i < picnumber; i++) {
                    params.put("picture" + (i + 1), Global.convertToBase64(((BitmapDrawable) report.getImages().get(i).getDrawable()).getBitmap()));
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        reqQueue.add(postRequest);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(context, "Nincs engedélyezve a GPS használata az eszközön.", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}
