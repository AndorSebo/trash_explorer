package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

}
