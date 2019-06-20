package mtsealove.com.github.BuslinkerDrivers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Permission;
import java.util.ArrayList;

public class LoadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        SystemUiTuner systemUiTuner=new SystemUiTuner(LoadActivity.this);
        systemUiTuner.setStatusBarWhite();

        requestPermission();
    }

    private void Login() {
                Intent intent=new Intent(LoadActivity.this, LoginActivity.class);
                startActivity(intent);
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
}
