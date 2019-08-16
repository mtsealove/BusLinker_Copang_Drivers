package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.google.firebase.iid.FirebaseInstanceId;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.MainActivity;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.Restful.LoginData;
import mtsealove.com.github.BuslinkerDrivers.Restful.Account;
import mtsealove.com.github.BuslinkerDrivers.SetIPActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;

public class LoginActivity extends AppCompatActivity {
    final int BusDriver = 0, BusCompany = 1;

    private TextView SignUpTV, findPwTV;
    //로그인 폼
    private EditText emailET, passwordET;
    private Button loginBtn;
    private CheckBox keepCB;    //로그인 유지
    boolean LogOuted;
    private ProgressDialog progressDialog;
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
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("로그인중입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();

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
            //서버 연결 및 로그인 실행
            PostLogin();
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

    private void PostLogin() {
        API api=new API();
        String Token=FirebaseInstanceId.getInstance().getToken();
        Call<Account> call=api.getRetrofitService().PostLogin(new LoginData(email, password, Token));
        Log.e("call", email+" "+password);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(response.isSuccessful()){ //데이터 반환성공 시
                    Account account =response.body();
                    if(account.isConfirmed()){    //승인이 된 사용자라면
                        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("Account", account);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "승인 대기중인 회원입니다", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "데이터를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("err", t.getCause().toString());
                Toast.makeText(LoginActivity.this, "서버연결에 실패하였습니다", Toast.LENGTH_SHORT).show();
            }
        });

    }

}