package balint.andor.trashexplorer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;
import balint.andor.trashexplorer.Classes.MenuItems;
import balint.andor.trashexplorer.Classes.User;

public class SettingsActivity extends AppCompatActivity {

    private Context context;
    private ImageView avatar;
    private int PICK_IMAGE = 1;
    private int requestCode = 0;
    private boolean changeAll;
    private boolean changedAvatar;
    private EditText oldPw,newPw,newPw2;
    private RequestQueue queue;
    private User u;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ImageButton menuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = SettingsActivity.this;
        CustomFont customFont = new CustomFont(context);
        u = User.getInstance();
        changedAvatar = false;
        changeAll = false;
        avatar = (ImageView) findViewById(R.id.avatar);
        ActionProcessButton changeAvatar = (ActionProcessButton) findViewById(R.id.changeAvatar);
        ActionProcessButton save = (ActionProcessButton) findViewById(R.id.save);
        ImageView eye0 = (ImageView) findViewById(R.id.soldPassword);
        ImageView eye1 = (ImageView) findViewById(R.id.snewPassword);
        ImageView eye2 = (ImageView) findViewById(R.id.scPassword);
        mDrawerList = (ListView) findViewById(R.id.listView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        MenuItems menuItems = new MenuItems((Activity) context);
        queue = Volley.newRequestQueue(context);
        oldPw  = (EditText) findViewById(R.id.oldPw);
        newPw  = (EditText) findViewById(R.id.newPw);
        newPw2 = (EditText) findViewById(R.id.cPw);
        changeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeAvatar();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        eye0.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent,oldPw);
                return true;
            }
        });
        eye1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent,newPw);
                return true;
            }
        });
        eye2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent,newPw2);
                return true;
            }
        });
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

    }

    private void changeAvatar(){
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

    private void save(){
        //Ha csak profil képet akarunk állítani
        if ("".equals(oldPw.getText().toString()) && "".equals(newPw.getText().toString()) &&
                "".equals(newPw2.getText().toString()) && changedAvatar)
            saveAvatar();
        //Ha nem töltötte ki a jelszó mezőket
        else if("".equals(oldPw.getText().toString()) || "".equals(newPw.getText().toString()) ||
                "".equals(newPw2.getText().toString()))
            Dialogs.showErrorDialog(getResources().getString(R.string.change_pass_error1),context).show();
        //Ha csak jelszót szeretnénk változtatni
        else if(!"".equals(oldPw.getText().toString()) && !"".equals(newPw.getText().toString()) &&
                !"".equals(newPw2.getText().toString()) && !changedAvatar)
            savePassword();
        //Ha mindkettőt szeretnénk változtatni
        else if(!"".equals(oldPw.getText().toString()) && !"".equals(newPw.getText().toString()) &&
                !"".equals(newPw2.getText().toString()) && changedAvatar){
            changeAll = true;
            saveAvatar();
            savePassword();
        }

    }

    private void saveAvatar(){
        connect(Global.getBaseUrl()+"/avatarchange");
    }
    private void savePassword(){
        if (!newPw.getText().toString().equals(newPw2.getText().toString()))
            Dialogs.showErrorDialog(getResources().getString(R.string.change_pass_error0),context).show();
        else if(newPw.getText().length()<6)
            Dialogs.showErrorDialog(getResources().getString(R.string.change_pass_error2),context).show();
        else{
            connect(Global.getBaseUrl()+"/passwordchange");
        }
    }
    private void connect(final String url){
        final String avatar64 = Global.convertToBase64(((BitmapDrawable) avatar.getDrawable()).getBitmap());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?token="+u.getToken(),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        String message;
                        if ("avatarchange".equals(url.split("/")[url.split("/").length-1]))
                            message = getResources()
                                    .getString(R.string.avatar_success);
                        else
                            message = getResources().getString(R.string.success_password);
                        final Dialog dialog = Dialogs.showSuccessDialog(message,context);
                        ActionProcessButton ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!changeAll)
                                    Global.openProfile(context);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message;
                        if ("avatarchange".equals(url.split("/")[url.split("/").length-1]))
                            message = getResources().getString(R.string.avatar_fail);
                        else
                            message = getResources().getString(R.string.failed_password);
                        Dialogs.showErrorDialog(message,context).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                if ("avatarchange".equals(url.split("/")[url.split("/").length-1]))
                    params.put("avatar",avatar64);
                else{
                    params.put("old",oldPw.getText().toString());
                    params.put("new",newPw.getText().toString());
                    params.put("confirm_new",newPw2.getText().toString());
                }
                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
        Dialogs.showLoadingDialog(context).show();
    }

    @Override
    public void onBackPressed() {
        Global.openProfile(context);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            Bitmap bitmap = (Bitmap)data.getExtras().get("data");
            avatar.setImageBitmap(bitmap);
            changedAvatar = true;
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE) {
                Uri selectedImageURI = data.getData();
                
                Picasso.with(context).load(selectedImageURI).into(avatar);
                changedAvatar = true;
            }
        }
    }
}
