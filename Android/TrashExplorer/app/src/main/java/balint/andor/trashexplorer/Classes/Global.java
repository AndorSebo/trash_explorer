package balint.andor.trashexplorer.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2017.10.10..
 */

public class Global {
    private static User u;

    public static String getBaseUrl() {
        return "http://trashexplorer.000webhostapp.com";
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
    public static void setId(int id) {
        u.setId(id);
    }
    public static void setToken(String token) {
        u.setToken(token);
    }
}
