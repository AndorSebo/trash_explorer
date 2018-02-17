package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;

import me.anwarshahriar.calligrapher.Calligrapher;

/**
 * Created by Andor on 2018.02.02..
 */

public class CustomFont {

    private static volatile CustomFont customFont = new CustomFont();

    private CustomFont(){}

    public static CustomFont getInstance(){
        if (customFont == null)
            customFont = new CustomFont();
        return customFont;
    }

    public void init(Context context) {
        Calligrapher calligrapher = new Calligrapher(context);
        calligrapher.setFont((Activity)context,"caverndreams.ttf",true);
    }
}
