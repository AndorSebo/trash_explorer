package balint.andor.trashexplorer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.dd.processbutton.iml.ActionProcessButton;

public class SingleReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionProcessButton send = (ActionProcessButton) findViewById(R.id.send);
        ActionProcessButton locate = (ActionProcessButton) findViewById(R.id.locate);
        TextView gpsNeed = (TextView) findViewById(R.id.gpsNeed);

        send.setVisibility(View.GONE);
        locate.setVisibility(View.GONE);
        gpsNeed.setVisibility(View.GONE);
    }
}
