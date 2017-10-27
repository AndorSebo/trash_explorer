package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.dd.processbutton.iml.ActionProcessButton;

public class MenuActivity extends AppCompatActivity {

    ActionProcessButton report, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        report = (ActionProcessButton) findViewById(R.id.reportButton);
        profile = (ActionProcessButton) findViewById(R.id.profileButton);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfile(MenuActivity.this);
            }
        });
    }
    void openProfile(Context ctx){
        Intent profile = new Intent(ctx, ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner, R.animator.scale_to_corner);
    }
}
