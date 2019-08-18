package mtsealove.com.github.BuslinkerDrivers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Design.RunInfoAdapter;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.Restful.Account;
import mtsealove.com.github.BuslinkerDrivers.Restful.RunInfo;
import mtsealove.com.github.BuslinkerDrivers.View.AccountView;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RunInfo> runInfos;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    EditText searchET;
    private Account account;
    private String ID, Name;
    private int cat;
    private ImageView searchBtn;
    private AccountView accountView;
    static DrawerLayout drawerLayout;
    TextView noWorkTV;
    private String key = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchET = findViewById(R.id.searchET);
        searchBtn = findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        Search();
                        break;
                }
                return false;
            }
        });

        //뷰 초기화
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        accountView = findViewById(R.id.accountView);
        drawerLayout = findViewById(R.id.drawerLayout);
        noWorkTV = findViewById(R.id.noWorkTV);

        //상태바
        SystemUiTuner sut = new SystemUiTuner(this);
        sut.setStatusBarWhite();

        //이전 액티비티로부터 데이터 수신
        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("Account");
        ID = account.getID();
        Name = account.getName();
        cat = account.getCat();
        accountView.setPassword(account.getPassword());

        accountView.setName(Name, cat);
        accountView.setUserID(ID);
        accountView.setUserName(Name);

        GetRunInfo();
    }

    ProgressDialog dialog;

    private void GetRunInfo() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("데이터를 가져오는 중입니다");
        dialog.setCancelable(false);
        dialog.show();

        API api = new API();
        Call<List<RunInfo>> call = api.getRetrofitService().GetRunInfoNext(ID, key);
        call.enqueue(new Callback<List<RunInfo>>() {
            @Override
            public void onResponse(Call<List<RunInfo>> call, Response<List<RunInfo>> response) {
                if (response.isSuccessful()) {
                    for (RunInfo runInfo : response.body()) {
                        //디스플레이 변경
                        runInfo.setRunDate(runInfo.getRunDate().substring(0, 10));
                        int wayloadCnt = runInfo.getWayloadAddrs().split(";;").length;
                        runInfo.setWayloadCnt(wayloadCnt);
                    }
                    RunInfoAdapter runInfoAdapter = new RunInfoAdapter(MainActivity.this, response.body());
                    recyclerView.setAdapter(runInfoAdapter);
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<RunInfo>> call, Throwable t) {
                Log.e("RunINFO", "오류 발생");
                dialog.dismiss();
            }
        });
    }



    //데이터 검색
    private void Search() {
        if (searchET.getText().toString().length() == 0)
            key = null;
        else
            key = searchET.getText().toString();
        GetRunInfo();
    }

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            long tempTime = System.currentTimeMillis();
            long intervalTime = tempTime - backPressedTime;

            if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
                super.onBackPressed();
            } else {
                backPressedTime = tempTime;
                Toast.makeText(getApplicationContext(), "뒤로가기를 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //메뉴 바 여닫기
    public static void openDrawer() {
        if (!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }

    public static void closeDrawer() {
        if (drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }
}
