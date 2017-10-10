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
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import balint.andor.trashexplorer.Classes.Global;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.animator.scale_from_corner,R.animator.scale_to_corner);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ImageView eye = (ImageView) findViewById(R.id.showPassword);
        TextView signUp = (TextView) findViewById(R.id.signUp);
        ActionProcessButton signInButton = (ActionProcessButton) findViewById(R.id.signIn);
        final EditText pwET = (EditText) findViewById(R.id.password);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRegistration();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Global.isNetwork(MainActivity.this))
                    Toast.makeText(MainActivity.this, "Van net", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        });
        eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Global.showPassword(event,pwET);
                return true;
            }
        });
    }
    private void openRegistration(){
        Intent registration = new Intent(MainActivity.this, RegActivity.class);
        startActivity(registration);
        finish();
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.animator.scale_from_corner,R.animator.scale_to_corner);
    }


}
