package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2018.02.01..
 */

public class Dialogs {
    private static FancyAlertDialog dialog;

    public static void showErrorDialog(String message, final Context context){
        Activity a = (Activity)context;
        dialog = new FancyAlertDialog.Builder(a)
                .setTitle("Hiba!")
                .setBackgroundColor(Color.parseColor("#F44336"))
                .setMessage(message)
                .setNegativeBtnText(a.getResources().getString(R.string.cancel))
                .setPositiveBtnBackground(Color.parseColor("#F44336"))
                .setPositiveBtnText("Ok")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_report, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .build();
    }
    public static void showLoadingDialog(){

    }
    public static void showSuccessDialog(){

    }
    public static void hideAlertDialog(){

    }
}
