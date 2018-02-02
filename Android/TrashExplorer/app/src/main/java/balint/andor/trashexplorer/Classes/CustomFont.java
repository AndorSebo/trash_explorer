package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by Andor on 2018.02.02..
 */

public class CustomFont {
    public CustomFont(Context context) {
        Calligrapher calligrapher = new Calligrapher(context);
        calligrapher.setFont((Activity)context,"caverndreams.ttf",true);
    }
}
