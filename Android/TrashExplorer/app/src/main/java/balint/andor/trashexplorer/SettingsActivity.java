package balint.andor.trashexplorer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import balint.andor.trashexplorer.Classes.CustomFont;
import balint.andor.trashexplorer.Classes.Global;

public class SettingsActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = SettingsActivity.this;
        CustomFont customFont = new CustomFont(context);
    }
    @Override
    public void onBackPressed() {
        Global.openProfile(context);
    }
}
