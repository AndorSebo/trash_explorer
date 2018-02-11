package balint.andor.trashexplorer;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

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
import balint.andor.trashexplorer.Classes.User;

public class SettingsActivity extends AppCompatActivity {

    private Context context;
    private ImageView avatar;
    private ActionProcessButton changeAvatar, save;
    private int PICK_IMAGE = 1;
    private int requestCode = 0;
    private boolean changedAvatar;
    private EditText oldPw,newPw,newPw2;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = SettingsActivity.this;
        CustomFont customFont = new CustomFont(context);
        changedAvatar = false;
        avatar = (ImageView) findViewById(R.id.avatar);
        changeAvatar = (ActionProcessButton) findViewById(R.id.changeAvatar);
        save = (ActionProcessButton) findViewById(R.id.save);
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
                save(avatar);
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

    private void save(ImageView avatar){
        //Ha csak profil képet akarunk állítani
        if ("".equals(oldPw.getText().toString()) && "".equals(newPw.getText().toString()) &&
                "".equals(newPw2.getText().toString()) && changedAvatar){
            saveAvatar();
        }
    }

    private void saveAvatar(){
        User u = User.getInstance();
        String url = Global.getBaseUrl()+"/avatarchange";
        final String avatar64 = Global.convertToBase64(((BitmapDrawable) avatar.getDrawable()).getBitmap());
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+"?token="+u.getToken(),
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Dialog dialog = Dialogs.showSuccessDialog(getResources()
                                .getString(R.string.avatar_success),context);
                        ActionProcessButton ok = dialog.findViewById(R.id.ok);
                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Global.openProfile(context);
                            }
                        });
                        dialog.show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialogs.showErrorDialog(getResources().getString(R.string.avatar_fail),context).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("avatar",avatar64);
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
