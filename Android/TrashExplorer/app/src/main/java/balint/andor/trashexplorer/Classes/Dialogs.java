package balint.andor.trashexplorer.Classes;

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

    private Dialogs() {}

    private static volatile Dialogs dialogs = new Dialogs();

    public static Dialogs getInstance(){
        if (dialogs == null)
            dialogs = new Dialogs();
        return dialogs;
    }

    public static Dialog showErrorDialog(String message, Context context){
        Dialog dialog = initDialog(context);
        TextView headerText = dialog.findViewById(R.id.headerText);
        TextView messageTV = dialog.findViewById(R.id.message);
        ImageView icon = dialog.findViewById(R.id.icon);
        headerText.setTypeface(face);
        messageTV.setTypeface(face);
        icon.setBackgroundResource(R.drawable.ic_error);
        headerText.setText(context.getResources().getString(R.string.oops));
        messageTV.setText(message);
        return dialog;
    }
    public static void showLoadingDialog(){

    }
    public static void showSuccessDialog(){

    }
    public static void hideAlertDialog(){

    }
    private static Dialog initDialog(final Context context){
        face = Typeface.createFromAsset(context.getAssets(),
                "caverndreams.ttf");
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.my_dialog);
        ActionProcessButton ok = dialog.findViewById(R.id.ok);
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
