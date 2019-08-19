package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Restful.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private String userID, password;
    private TextView emailTV, nameTV, birthTV, genderTV, companyTV;
    private EditText newPwET, newPwConfirmET, contactET, prET, carTypeET, carNumberET, PwET;
    Button EditBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        InitView();
        InitData();
        newPwET.addTextChangedListener(newPasswordWatcher);
        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateAccount();
            }
        });
    }

    //뷰 매칭
    private void InitView() {
        emailTV=findViewById(R.id.emailTV);
        nameTV=findViewById(R.id.nameTV);
        birthTV=findViewById(R.id.birthTV);
        genderTV=findViewById(R.id.genderTV);
        companyTV=findViewById(R.id.companyTV);
        newPwET=findViewById(R.id.newPwET);
        newPwConfirmET=findViewById(R.id.newPwConfirmET);
        contactET=findViewById(R.id.contactET);
        prET=findViewById(R.id.prET);
        carTypeET=findViewById(R.id.carTypeET);
        carNumberET=findViewById(R.id.carNumberET);
        PwET=findViewById(R.id.pwET);
        EditBtn=findViewById(R.id.EditBtn);
    }

    ProgressDialog dialog;
    //데이터 읽고 표시
    private void InitData() {
        dialog=new ProgressDialog(this);
        dialog.setMessage("정보를 읽는 중입니다");
        dialog.setCancelable(false);
        dialog.show();
        API api=new API();
        userID=getIntent().getStringExtra("ID");
        password=getIntent().getStringExtra("password");
        //로그인 API 재활용
        Call<Account> call=api.getRetrofitService().PostLogin(new LoginData(userID, password));
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(response.isSuccessful()) {
                    Account account=response.body();
                    emailTV.setText(account.getID());
                    nameTV.setText(account.getName());
                    birthTV.setText(account.getBirth().substring(0, 10));
                    if(account.getGender()=='m')
                        genderTV.setText("남성");
                    else genderTV.setText("여성");
                    companyTV.setText(account.getCompanyID());
                    contactET.setText(account.getContact());
                    if(account.getPR()!=null)
                        prET.setText(account.getPR());
                    carTypeET.setText(account.getCarType());
                    carNumberET.setText(account.getCarNum());
                } else {
                    Toast.makeText(AccountActivity.this, "데이터를 받아오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "서버연결에 실패하였습니다", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void UpdateAccount() {
        String newPassword = null;
        //비밀번호를 변경할 경우
        if(newPwET.getText().toString().length()!=0) {
            newPassword=newPwET.getText().toString();
            if(!InputChecker.CheckPassword(newPassword)){
                Toast.makeText(this, "비밀번호는 8자 이상 영문 대소문자, 숫자 및 특수기호를 포함해야 합니다",Toast.LENGTH_SHORT).show();
                return;
            } else if(!newPassword.equals(newPwConfirmET.getText().toString())){
                Toast.makeText(this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(!PwET.getText().toString().equals(password)){
            Toast.makeText(this, "기존 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        PostUpdate(newPassword, contactET.getText().toString(), prET.getText().toString(), carTypeET.getText().toString(), carNumberET.getText().toString());
    }

    private ProgressDialog progressDialog;
    private void PostUpdate(@Nullable String newPassword, String Contact, String pr, String CarType, String CarNumber) {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("정보를 수정중입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();
        API api=new API();
        Call<PostResult> call=api.getRetrofitService().UpdateAccount(new AccountUpdate(userID, newPassword, Contact, pr, CarType, CarNumber));
        call.enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {

                if(response.isSuccessful()){
                    Log.e("결과", response.body().getResult());
                    if(response.body().getResult().equals("OK"))
                        Toast.makeText(AccountActivity.this, "정보가 수정되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                Toast.makeText(AccountActivity.this, "서버연결에 실패했습니다", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    //새 비밀번호가 입력되었을 경우의 이벤트
    private TextWatcher newPasswordWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.length()!=0)
                newPwConfirmET.setVisibility(View.VISIBLE);
            else
                newPwConfirmET.setVisibility(View.GONE);
        }
        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
}
