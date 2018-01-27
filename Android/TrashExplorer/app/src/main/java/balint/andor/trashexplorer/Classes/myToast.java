package balint.andor.trashexplorer.Classes;

import android.content.Context;
import com.singh.daman.gentletoast.GentleToast;
import balint.andor.trashexplorer.R;

public class myToast{
    public static void Error(String msg, Context ctx){
        GentleToast.with(ctx).longToast(msg)
                .setTextColor(R.color.white)
                .setBackgroundColor(R.color.error_red)
                .setBackgroundRadius(16)
                .setStrokeColor(R.color.error_border)
                .setStrokeWidth(4)
                .setImage(R.drawable.ic_report)
                .show();
    }
}
