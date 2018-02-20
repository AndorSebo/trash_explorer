package balint.andor.trashexplorer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.dd.processbutton.iml.ActionProcessButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.MenuHeader;
import balint.andor.trashexplorer.Classes.MenuItems;
import balint.andor.trashexplorer.Classes.User;

public class SingleReportActivity extends AppCompatActivity implements OnMapReadyCallback {


    private MapFragment mapFragment;
    private RequestQueue reqQueue;
    private User user;
    private double latitude, longitude;
    private Dialogs dialogs;
    private ArrayList<String> imgUrls;
    private int width;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ImageButton menuButton;
    private TextView headerTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final Context ctx = SingleReportActivity.this;
        CustomFont.getInstance().init(ctx);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        TextView gpsNeed = (TextView) findViewById(R.id.gpsNeed);
        View blackMask = findViewById(R.id.blackMask);
        TextView coordNotFound = (TextView) findViewById(R.id.coordNotFound);
        EditText description = (EditText) findViewById(R.id.description);
        mDrawerList = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        headerTv = (TextView) findViewById(R.id.headerText);
        MenuItems menuItems = new MenuItems((Activity)ctx);
        dialogs = Dialogs.getInstance();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        user = User.getInstance();
        ImageView[] imageViews = new ImageView[4];

        for (int i = 0; i < imageViews.length; i++) {
            int resID = getResources().getIdentifier(("img" + i), "id", getPackageName());
            imageViews[i] = (ImageView) findViewById(resID);
            imageViews[i].setImageResource(R.drawable.image_nf);
        }

        headerTv.setText(getResources().getString(R.string.my_reports));

        if (Global.isNetwork(ctx))
            getReport(blackMask, coordNotFound, String.valueOf(getIntent().getExtras().getInt("id")), description, imageViews);
        else
            Global.networkNotFound(ctx);

        send.setVisibility(View.GONE);
        locate.setVisibility(View.GONE);
        gpsNeed.setVisibility(View.GONE);
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
                Global.menu(i,ctx);
            }
        });
        mDrawerList.addHeaderView(MenuHeader.getInstance().init(ctx));
    }

    void getReport(final View blackMask, final TextView coordNotFound,
                   String reportid, final EditText description, final ImageView[] imageViews) {
        Dialogs.showLoadingDialog(SingleReportActivity.this).show();
        description.setFocusable(false);
        description.setClickable(true);
        description.setLongClickable(false);
        reqQueue = Volley.newRequestQueue(this);
        String url = Global.getBaseUrl() + "/getreport";
        String token = "?token=" + user.getToken();
        reportid = "&reportid=" + reportid;
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url + token + reportid, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                imgUrls = new ArrayList<>();
                                JSONArray jsonArray = response.getJSONArray("data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                latitude = jsonObject.getDouble("latitude");
                                longitude = jsonObject.getDouble("longitude");
                                hideMap(blackMask, coordNotFound, latitude, longitude);
                                description.setText(jsonObject.getString("description"));
                                jsonArray = jsonObject.getJSONArray("mini_image");
                                getImgs(jsonArray, imageViews);
                                dialogs.hideAlertDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialogs.showErrorDialog(getResources().getString(R.string.wrong), getBaseContext());
                    }
                }
        );
        reqQueue.add(getRequest);
        reqQueue.start();
    }

    void getImgs(JSONArray jsonArray, final ImageView[] imgViews) throws JSONException {
        final Context ctx = SingleReportActivity.this;
        final ImagePopup imagePopup = new ImagePopup(ctx);
        getScreenResolution();
        imagePopup.setBackgroundColor(getResources().getColor(R.color.button_background));
        imagePopup.setFullScreen(true);
        imagePopup.setImageOnClickClose(true);
        imagePopup.setMaxHeight(width);
        imagePopup.setMaxWidth(width);
        imagePopup.setHideCloseIcon(true);
        for (int i = 0; i < jsonArray.length(); i++) {
            final String url = jsonArray.getJSONObject(i).getString("mini_image");
            imgUrls.add(Global.getBaseUrl() + "/" + url);
            Picasso.with(ctx).load(imgUrls.get(i)).into(imgViews[i]);
            imgViews[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String[] urlArray = url.split("/");
                    String normalURL = urlArray[0] + "/normal/" + urlArray[2];
                    imagePopup.initiatePopupWithPicasso(Global.getBaseUrl() + "/" + normalURL);
                    imagePopup.viewPopup();
                }
            });
        }

    }

    void hideMap(View blackMask, TextView coordNotFound, double latitude, double longitude) {
        if (latitude == 1 && longitude == 1) {
            blackMask.setVisibility(View.VISIBLE);
            coordNotFound.setVisibility(View.VISIBLE);
        } else {
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

    void openMyReports(Context ctx) {
        Intent myReports = new Intent(ctx, MyReportsActivity.class);
        startActivity(myReports);
        finish();
    }

    @Override
    public void onBackPressed() {
        openMyReports(SingleReportActivity.this);
    }

    void getScreenResolution() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
    }
}
