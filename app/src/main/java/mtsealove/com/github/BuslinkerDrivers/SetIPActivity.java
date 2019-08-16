package mtsealove.com.github.BuslinkerDrivers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.*;

public class SetIPActivity extends AppCompatActivity {
    public static String IP;
    private File file;
    Button confirmBtn;
    EditText ipET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_ip);
        ipET=findViewById(R.id.ipET);
        confirmBtn=findViewById(R.id.confirmBtn);

        file=new File(getFilesDir()+"ip.dat");
        try{
            ipET.setText(IP);
        }catch (Exception e) {
            e.printStackTrace();
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteIP();
            }
        });
    }

    void WriteIP(){
        String IP=ipET.getText().toString();
        try {
            BufferedWriter bw=new BufferedWriter(new FileWriter(file));
            bw.write(IP);
            bw.flush();
            bw.close();
            SetIPActivity.IP=IP;
            finish();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
