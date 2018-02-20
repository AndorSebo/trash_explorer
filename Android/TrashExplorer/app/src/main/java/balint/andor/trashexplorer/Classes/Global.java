package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import balint.andor.trashexplorer.MainActivity;
import balint.andor.trashexplorer.ProfileActivity;
import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2017.10.10..
 */

public class Global {

    public static String getBaseUrl() {
        return "http://www.trashexplorer.nhely.hu";
    }

    public static boolean isNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public static void showPassword(MotionEvent event,EditText pwET){
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                pwET.setTransformationMethod(PasswordTransformationMethod.getInstance());
                break;
            case MotionEvent.ACTION_DOWN:
                pwET.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                break;
        }
    }
    public static void networkNotFound(Context context){
        Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();

    }
    public static void openProfile(Context ctx){
        Intent profile = new Intent(ctx, ProfileActivity.class);
        ctx.startActivity(profile);
        ((Activity) ctx).finish();
    }
    public static String convertToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 45, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
    public static void logout(final Context ctx){
        RequestQueue reqQueue = Volley.newRequestQueue(ctx);
        User user = User.getInstance();
        user.setName("");
        String token = user.getToken();
        String url = Global.getBaseUrl() + "/logout";
        Dialogs.showLoadingDialog(ctx).show();
        StringRequest postRequest = new StringRequest(Request.Method.GET, url + "?token=" + token,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent logout = new Intent(ctx, MainActivity.class);
                        ctx.startActivity(logout);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Dialogs.showErrorDialog(ctx.getResources().getString(R.string.wrong), ctx).show();
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqQueue.add(postRequest);
    }
    public static void menu(int i, Context ctx){
        ArrayList<Activity> activities;
        MenuItems menuItems = new MenuItems((Activity)ctx);
        activities = menuItems.getActivities();
        if (i<activities.size()) {
            Intent intent = new Intent(ctx, activities.get(i).getClass());
            ctx.startActivity(intent);
        }else
            logout(ctx);
    }

}
