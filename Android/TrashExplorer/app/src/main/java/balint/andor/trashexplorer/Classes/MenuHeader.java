package balint.andor.trashexplorer.Classes;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2018.02.17..
 */

public class MenuHeader {

    private static volatile MenuHeader menuHeader = new MenuHeader();

    private MenuHeader(){}

    public View init(Context context){
        Typeface face = Typeface.createFromAsset(context.getAssets(),"caverndreams.ttf");
        View header = View.inflate(context, R.layout.menu_header,null);
        User u = User.getInstance();
        TextView name = header.findViewById(R.id.nameTv);
        TextView email = header.findViewById(R.id.emailTv);
        ImageView avatar = header.findViewById(R.id.avatar);
        name.setText(u.getName());
        name.setTypeface(face);
        email.setText(u.getEmail());
        email.setTypeface(face);
        Picasso.with(context).load(Global.getBaseUrl()+"/"+u.getAvatar())
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(avatar);
        return header;
    }

    public static MenuHeader getInstance(){
        if(menuHeader == null)
            menuHeader = new MenuHeader();
        return menuHeader;
    }

}
