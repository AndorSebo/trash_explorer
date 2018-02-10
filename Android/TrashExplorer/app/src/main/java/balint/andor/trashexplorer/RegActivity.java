package balint.andor.trashexplorer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Dialogs;
import balint.andor.trashexplorer.Classes.Global;

public class RegActivity extends AppCompatActivity {

    private RequestQueue reqQueue;
    private Dialogs dialogs;
    private EditText username, email, password,repassword;
    private ImageView avatar;
    private int PICK_IMAGE = 1;
    private int requestCode = 0;
    private boolean changedAvatar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        CustomFont customFont = new CustomFont(RegActivity.this);
        TextView login = (TextView) findViewById(R.id.backLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        password = (EditText) findViewById(R.id.password);
        ImageView showPW = (ImageView) findViewById(R.id.showPassword);
        repassword = (EditText) findViewById(R.id.cpassword);
        ImageView cshowPW = (ImageView) findViewById(R.id.cshowPassword);
        ActionProcessButton regButton = (ActionProcessButton) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        avatar = (ImageView) findViewById(R.id.avatar);
        final Context ctx = RegActivity.this;
        dialogs = Dialogs.getInstance();
        changedAvatar = false;

        reqQueue = Volley.newRequestQueue(this);
        reqQueue.start();


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetwork(ctx)) {
                    registration();
                    Dialogs.showLoadingDialog(RegActivity.this).show();
                } else {
                    Global.networkNotFound(ctx);
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAvatar();

            }
        });

        showPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, password);
                return true;
            }
        });
        cshowPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent, repassword);
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

    void registration() {
        String url = Global.getBaseUrl() + "/signup";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        Dialogs.showSuccessDialog(getResources().getString(R.string.success_register),RegActivity.this).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", error.toString());
                        Dialogs.showErrorDialog(getString(R.string.wrong), getBaseContext());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", username.getText().toString());
                params.put("email", email.getText().toString());
                params.put("password", password.getText().toString());
                params.put("repassword", repassword.getText().toString());
                if(changedAvatar)
                    params.put("avatar",Global.convertToBase64(((BitmapDrawable)avatar.getDrawable()).getBitmap()));
                else
                    params.put("avatar","");
                return params;
            }
        };
        reqQueue.add(postRequest);
    }

    private void selectAvatar(){
        final Dialog dialog = Dialogs.showImageDialog(RegActivity.this);
        ImageView camera = dialog.findViewById(R.id.camera);
        ImageView gallery = dialog.findViewById(R.id.gallery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Build.VERSION.SDK_INT >=23 && ContextCompat.checkSelfPermission(RegActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RegActivity.this, new String[]{
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
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                dialog.dismiss();
            }
        });
        dialog.show();
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
                Picasso.with(RegActivity.this).load(selectedImageURI).into(avatar);
                changedAvatar = true;
            }
        }
    }
}
