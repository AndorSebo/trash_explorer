package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.MyReportsActivity;
import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2017.11.07..
 */

public class ReportAdapter extends ArrayAdapter<Report> {
    RequestQueue queue;

    public ReportAdapter(Context context, ArrayList<Report> descriptions) {
        super(context, R.layout.report_item, descriptions);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater reportInflater = LayoutInflater.from(getContext());
        View reportView = reportInflater.inflate(R.layout.report_item, parent, false);
        final String description = getItem(position).getDescription();
        ImageView delete = reportView.findViewById(R.id.delete);
        TextView descriptionTV = reportView.findViewById(R.id.description);
        queue = Volley.newRequestQueue(getContext());
        queue.start();
        if (description != null)
            descriptionTV.setText(description);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return reportView;
    }

    void delete(final int reportid, final Dialogs dialogs){
        User u = Global.getUser();
        String url = Global.getBaseUrl()+"/deletereport";
        String token = "?token="+u.getToken();
        StringRequest postRequest = new StringRequest(Request.Method.POST, url+token,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        Intent refresh = new Intent (getContext(),MyReportsActivity.class);
                        (getContext()).startActivity(refresh);
                        ((Activity)getContext()).finish();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response", error.toString());
                        //Dialogs.showErrorDialog(getContext().getString(R.string.wrong), getBaseContext());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams(){
                Map<String, String>  params = new HashMap<>();
                params.put("reportid",String.valueOf(reportid));
                return params;
            }
        };
        queue.add(postRequest);
    }

}
