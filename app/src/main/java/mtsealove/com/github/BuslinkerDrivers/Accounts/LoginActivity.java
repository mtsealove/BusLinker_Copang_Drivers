package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.MainActivity;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.SetIPActivity;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.*;
import java.net.URISyntaxException;

public class LoginActivity extends AppCompatActivity {
    final int BusDriver = 0, BusCompany = 1;

    private TextView SignUpTV, findPwTV;
    //로그인 폼
    private EditText emailET, passwordET;
    private Button loginBtn;
    private CheckBox keepCB;    //로그인 유지
    boolean LogOuted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SignUpTV = findViewById(R.id.signupTV);
        SignUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });

        loginBtn = findViewById(R.id.loginBtn);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        keepCB = findViewById(R.id.keepCB);
        findPwTV=findViewById(R.id.findPwTV);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        SystemUiTuner systemUiTuner=new SystemUiTuner(this);
        systemUiTuner.setStatusBarWhite();

        findPwTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this, SetIPActivity.class);
                startActivity(intent);
            }
        });

        LogOuted=getIntent().getBooleanExtra("Logout", false);
        if(!LogOuted)
            ReadAccount();
        else{
            WriteAccount(null, null);
        }
    }

    private String email, password;

    //로그인
    protected void Login() {
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        boolean keep = keepCB.isChecked();

        //입력  무결성 체크
        if (email.length() == 0) Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 0) Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {
            if(keep){   //ID 비번 저장
                WriteAccount(email, password);
            } else{
                WriteAccount(null, null);
            }

            ConnectSocket();
        }
    }

    private File acFile;
    //단말에 ID와 비밀번호 저장
    private void WriteAccount(String email, String password){
        acFile=new File(getFilesDir()+"ac.dat");
        if(email!=null) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(acFile));
                bw.write(email);

                bw.newLine();
                bw.write(password);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {   //사용자 정보 삭제
                BufferedWriter bw = new BufferedWriter(new FileWriter(acFile, false));
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //저장된 Pw 읽기
    private void ReadAccount(){
        acFile=new File(getFilesDir()+"ac.dat");
        try {
            BufferedReader br=new BufferedReader(new FileReader(acFile));
            String email=br.readLine();
            String pw=br.readLine();
            br.close();
            //로그인 진행
            if(email!=null&&email.length()!=0) {
                emailET.setText(email);
                passwordET.setText(pw);
                keepCB.setChecked(true);
                loginBtn.performClick();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //회원가입 액티비티로 이동
    protected void SignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    //소켓
    private Socket mSocket;
    private ProgressDialog progressDialog;
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setMessage("로그인중");
                    progressDialog.show();
                }
            });

            JSONObject data = new JSONObject();
            try {
                data.put("email", email);
                data.put("password", password);
                //토큰 전송
                data.put("tokken", FirebaseInstanceId.getInstance().getToken());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket.emit("Login", data);
        }
    };

    String TAG = "결과";
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 데이터 파싱
            try {

                JSONObject receivedData = (JSONObject) args[0];
                String Name = receivedData.getString("Name");
                String ID = receivedData.getString("ID");
                int confirmed=1;
                int cat = receivedData.getInt("cat");
                Log.d(TAG, Name);
                Log.d(TAG, ID);
                mSocket.disconnect();

                try {
                    confirmed=receivedData.getInt("Confirmed");
                } catch (Exception e){
                    e.printStackTrace();
                }

                //아직 회원 수락이 되지 않았다면
                if(confirmed!=1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "회원가입 대기중입니다.\n승인 후 이용해 주세요", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (cat==3) { //ID 또는 비밀번호 오류
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {    //메인 화면으로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("cat", cat);
                    intent.putExtra("ID", email);
                    intent.putExtra("Name", Name);
                    startActivity(intent);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }


        }
    };

    private void ConnectSocket() {
        try {
            mSocket = IO.socket(SetIPActivity.IP);   //서버 주소
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("serverMessage", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}