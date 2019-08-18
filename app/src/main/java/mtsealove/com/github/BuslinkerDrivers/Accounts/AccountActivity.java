package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.Restful.Account;
import mtsealove.com.github.BuslinkerDrivers.Restful.LoginData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountActivity extends AppCompatActivity {
    private String userID, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Init();
    }

    private void Init() {
        API api=new API();
        userID=getIntent().getStringExtra("ID");
        password=getIntent().getStringExtra("password");
        Call<Account> call=api.getRetrofitService().PostLogin(new LoginData(userID, password));
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Log.e("계정정보", response.body().toString());
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

            }
        });
    }
}
