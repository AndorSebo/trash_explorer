package balint.andor.trashexplorer.Classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * Created by Andor on 2017.10.10..
 */

public class Global {

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
}
