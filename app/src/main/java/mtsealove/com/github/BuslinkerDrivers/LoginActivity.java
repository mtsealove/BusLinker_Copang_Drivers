package mtsealove.com.github.BuslinkerDrivers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import mtsealove.com.github.BuslinkerDrivers.Accounts.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

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

    //로그인
    protected void Login() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        boolean keep = keepCB.isChecked();

        //입력  무결성 체크
        if (email.length() == 0) Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 0) Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else {

        }

    }

    //회원가입 액티비티로 이동
    protected void SignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private class SqlTask extends AsyncTask<String, Void, String > {
        ProgressDialog progressDialog;

        //로그인 준비
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(LoginActivity.this, "로그인 중입니다", null ,true, true);
        }

        
        @Override
        protected String doInBackground(String... strings) {
            return null;
        }
    }
}
