package balint.andor.trashexplorer.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2017.11.10..
 */

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, ArrayList<User> users) {
        super(context, R.layout.users_list_item, users);
    }

    public View getView(int position, View convertView,ViewGroup parent) {
        LayoutInflater usersInflater = LayoutInflater.from(getContext());
        View usersView = usersInflater.inflate(R.layout.users_list_item, parent, false);
        TextView nameTv = usersView.findViewById(R.id.nameTv);
        String name = getItem(position).getName();
        nameTv.setText(name);
        return usersView;
    }
}
