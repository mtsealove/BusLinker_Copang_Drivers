package mtsealove.com.github.BuslinkerDrivers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();
        Login();
    }

    private void Login() {
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(LoadActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 700);
    }
}
