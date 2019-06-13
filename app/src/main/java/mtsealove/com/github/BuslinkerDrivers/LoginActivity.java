package mtsealove.com.github.BuslinkerDrivers;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Accounts.SignUpActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {
    final String IP = "http://192.168.1.50:3300";

    private TextView SignUpTV;
    //로그인 폼
    private EditText emailET, passwordET;
    private Button loginBtn;
    private CheckBox keepCB;    //로그인 유지

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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

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
            ConnectSocket();
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
                    progressDialog.setMessage("로딩중");
                    progressDialog.show();
                }
            });

            JSONObject data = new JSONObject();
            try {
                data.put("email", email);
                data.put("password", password);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Log.e("JSON", data.toString());
            }
            mSocket.emit("Login", data);
        }
    };

    String TAG = "결과";
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            try {

                JSONObject receivedData = (JSONObject) args[0];
                String Name = receivedData.getString("Name");
                String ID = receivedData.getString("ID");
                Log.d(TAG, Name);
                Log.d(TAG, ID);
                mSocket.disconnect();

                if (ID.equals("none")) { //쿼리 실패
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {    //메인 화면으로 이동
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("name", Name);
                    startActivity(intent);
                    finish();
                }

            } catch (JSONException e) {
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
        Log.e("연결 메서드", "실행");
        try {
            mSocket = IO.socket("http://192.168.10.31:3000");   //서버 주소
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("serverMessage", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}