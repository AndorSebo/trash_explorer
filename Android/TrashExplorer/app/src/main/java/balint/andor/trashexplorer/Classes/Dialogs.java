package balint.andor.trashexplorer.Classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2018.02.01..
 */

public class Dialogs {

    private static Typeface face;
    private static TextView headerText;
    private static TextView messageTV;
    private static ImageView icon;

    private Dialogs() {}

    @SuppressLint("StaticFieldLeak")
    private static volatile Dialogs dialogs = new Dialogs();

    public static Dialogs getInstance(){
        if (dialogs == null)
            dialogs = new Dialogs();
        return dialogs;
    }

    public static Dialog showErrorDialog(String message, Context context){
        Dialog dialog = initDialog(context);
        icon.setBackgroundResource(R.drawable.ic_error);
        headerText.setText(context.getResources().getString(R.string.oops));
        messageTV.setText(message);
        return dialog;
    }
    public static void showLoadingDialog(){

    }
    public static Dialog showSuccessDialog(Context context){
        Dialog dialog = initDialog(context);
        icon.setBackgroundResource(R.drawable.ic_success);
        headerText.setText(context.getResources().getString(R.string.oops));
        messageTV.setText("");
        return dialog;
    }
    public static void hideAlertDialog(){

    }
    private static Dialog initDialog(final Context context){
        face = Typeface.createFromAsset(context.getAssets(),
                "caverndreams.ttf");
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.my_dialog);
        headerText = dialog.findViewById(R.id.headerText);
        messageTV = dialog.findViewById(R.id.message);
        icon = dialog.findViewById(R.id.icon);
        ActionProcessButton ok = dialog.findViewById(R.id.ok);
        headerText.setTypeface(face);
        messageTV.setTypeface(face);
        ok.setTypeface(face);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

}
