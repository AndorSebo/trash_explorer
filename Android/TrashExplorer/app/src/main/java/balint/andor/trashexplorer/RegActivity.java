package balint.andor.trashexplorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import balint.andor.trashexplorer.Classes.Global;

public class RegActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TextView login = (TextView) findViewById(R.id.backLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        final EditText pwET = (EditText) findViewById(R.id.password);
        ImageView showPW = (ImageView) findViewById(R.id.showPassword);
        final EditText cpwET = (EditText) findViewById(R.id.cpassword);
        ImageView cshowPW = (ImageView) findViewById(R.id.cshowPassword);

        showPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent,pwET);
                return true;
            }
        });
        cshowPW.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Global.showPassword(motionEvent,cpwET);
                return true;
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent login = new Intent(RegActivity.this,MainActivity.class);
        startActivity(login);
        finish();
    }
}
