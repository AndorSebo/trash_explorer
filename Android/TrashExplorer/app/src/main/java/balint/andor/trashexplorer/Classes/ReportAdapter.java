package balint.andor.trashexplorer.Classes;

import android.app.Activity;
import android.app.Dialog;
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
import com.dd.processbutton.iml.ActionProcessButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import balint.andor.trashexplorer.MyReportsActivity;
import balint.andor.trashexplorer.R;

/**
 * Created by Andor on 2017.11.07..
 */

public class ReportAdapter extends ArrayAdapter<Report> {
    private RequestQueue queue;
    private Dialog dialog;
    private ActionProcessButton yes,no;

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
                dialog = Dialogs.showDeleteDialog(getContext(),getContext().getResources().getString(R.string.del_report));
                no = dialog.findViewById(R.id.no);
                yes = dialog.findViewById(R.id.yes);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delete(getItem(position).getReport_id());
                        Log.d("DELETE",getItem(position).getDescription());
                    }
                });
                dialog.show();

            }
        });
        return reportView;
    }

    void delete(final int reportid){
        User u = User.getInstance();
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
                       Dialogs.showErrorDialog(getContext().getString(R.string.wrong),getContext());
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
