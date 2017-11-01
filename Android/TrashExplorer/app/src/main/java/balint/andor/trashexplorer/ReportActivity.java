package balint.andor.trashexplorer;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

public class ReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    void openProfile(Context ctx){
        Intent profile = new Intent(ctx, ProfileActivity.class);
        startActivity(profile);
        finish();
    }

    @Override
    public void onBackPressed() {
        openProfile(ReportActivity.this);
    }
}
