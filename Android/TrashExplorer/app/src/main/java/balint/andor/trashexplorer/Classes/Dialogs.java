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
    private static ActionProcessButton ok;
    private static Dialog dialog;

    private Dialogs() {}

    @SuppressLint("StaticFieldLeak")
    private static volatile Dialogs dialogs = new Dialogs();

    public static Dialogs getInstance(){
        if (dialogs == null)
            dialogs = new Dialogs();
        return dialogs;
    }

    private static Dialog initDialog(final Context context){
        face = Typeface.createFromAsset(context.getAssets(),"caverndreams.ttf");
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.my_dialog);
        headerText = dialog.findViewById(R.id.headerText);
        messageTV = dialog.findViewById(R.id.message);
        icon = dialog.findViewById(R.id.icon);
        ok = dialog.findViewById(R.id.ok);
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

    public static Dialog showErrorDialog(String message, Context context){
        dialog.dismiss();
        dialog = initDialog(context);
        icon.setBackgroundResource(R.drawable.ic_error);
        headerText.setText(context.getResources().getString(R.string.oops));
        messageTV.setText(message);
        return dialog;
    }
    public static Dialog showLoadingDialog(Context context){
        dialog = initDialog(context);
        dialog.setCancelable(false);
        ok.setVisibility(View.GONE);
        icon.setBackgroundResource(R.drawable.ic_loading);
        headerText.setText(context.getResources().getString(R.string.Loading));
        return dialog;
    }
    public static Dialog showSuccessDialog(String message, Context context){
        dialog.dismiss();
        dialog = initDialog(context);
        icon.setBackgroundResource(R.drawable.ic_success);
        headerText.setText(context.getResources().getString(R.string.success));
        messageTV.setText(message);
        return dialog;
    }
    public static void hideAlertDialog(){
        dialog.dismiss();
    }
    public static Dialog showImageDialog(Context context){
        face = Typeface.createFromAsset(context.getAssets(),"caverndreams.ttf");
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.image_dialog);
        TextView headerText = dialog.findViewById(R.id.headerText);
        headerText.setTypeface(face);
        return dialog;
    }
    public static  Dialog showDeleteDialog(Context context,String message){
        face = Typeface.createFromAsset(context.getAssets(),"caverndreams.ttf");
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.delete_dialog);
        TextView messageTV = dialog.findViewById(R.id.message);
        messageTV.setText(message);
        messageTV.setTypeface(face);
        return dialog;
    }

}
