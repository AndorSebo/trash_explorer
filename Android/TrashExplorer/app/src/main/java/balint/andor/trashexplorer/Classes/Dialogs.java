package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;

import balint.andor.trashexplorer.MyReportsActivity;
import balint.andor.trashexplorer.R;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Andor on 2017.11.04..
 */

public class Dialogs {
    private Context ctx;
    private SweetAlertDialog sweetAlertDialog;
    public Dialogs(Context ctx) {
        this.ctx = ctx;
    }
    public void showLoadingDialog(){
        sweetAlertDialog = new SweetAlertDialog(ctx, SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitleText(ctx.getString(R.string.wait))
                .getProgressHelper().setBarColor(ctx.getResources().getColor(R.color.blue_normal));
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
    }
    public void showSuccessDialog(){
        hideAlertDialog();
        sweetAlertDialog = new SweetAlertDialog(ctx, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(ctx.getString(R.string.success))
        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (ctx.toString().split("@")[0].equals("balint.andor.trashexplorer.ReportActivity"))
                    ((Activity)ctx).onBackPressed();
                else
                    hideAlertDialog();
            }
        }).show();

    }
    public void hideAlertDialog(){
        if (sweetAlertDialog != null)
            sweetAlertDialog.dismiss();
    }
    public void showErrorDialog(String message){
        hideAlertDialog();
        sweetAlertDialog = new SweetAlertDialog(ctx, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(ctx.getString(R.string.oops)).setContentText(message).show();
    }

}
