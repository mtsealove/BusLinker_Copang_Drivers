package mtsealove.com.github.BuslinkerDrivers;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import mtsealove.com.github.BuslinkerDrivers.Accounts.LoginActivity;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;

import java.io.*;
import java.util.ArrayList;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoadActivity extends AppCompatActivity {
File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();

        requestPermission();
        ReadIP();
    }

    private void Login() {
                Intent intent=new Intent(LoadActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
    }

    private void requestPermission() {  //권한 체크
        TedPermission.with(this)
                .setRationaleMessage("애플리케이션을 사용하려면 권한을 허용해야 합니다")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .setPermissionListener(permissionListener)
                .check();
    }

    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Login();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(LoadActivity.this, "권한이 거부되었습니다.\n잠시 후 프로그램이 종료됩니다", Toast.LENGTH_LONG).show();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            },2000);
        }
    };

    void ReadIP(){  //IP 읽기
        file=new File(getFilesDir()+"ip.dat");
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String tmp="";
            while ((tmp=bufferedReader.readLine())!=null){
                SetIPActivity.IP=tmp;
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
