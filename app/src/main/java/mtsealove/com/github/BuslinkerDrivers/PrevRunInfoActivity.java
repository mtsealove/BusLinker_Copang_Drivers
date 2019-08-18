package mtsealove.com.github.BuslinkerDrivers;

import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.View.AccountView;
import mtsealove.com.github.BuslinkerDrivers.Design.RunInfoAdapter;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.Restful.RunInfo;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class PrevRunInfoActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    EditText searchET;
    ImageView searchBtn;
    private String ID, Name, key=null;
    private int cat;
    private AccountView accountView;
    static DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_run_info);

        //이전 액티비티로부터 데이터 수신
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");

        searchET = findViewById(R.id.searchET);
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
        searchBtn=findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Search();
            }
        });

        //뷰 초기화
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        accountView = findViewById(R.id.accountView);
        drawerLayout = findViewById(R.id.drawerLayout);

        accountView.setName(Name, cat);

        //상태바
        SystemUiTuner sut = new SystemUiTuner(this);
        sut.setStatusBarWhite();

        Name = intent.getStringExtra("Name");
        cat = intent.getIntExtra("cat", 0);

        accountView.setName(Name, cat);
        accountView.setUserID(ID);

        GetPrevRunInfo();
    }

    ProgressDialog dialog;
    private void GetPrevRunInfo() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("데이터를 가져오는 중입니다");
        dialog.setCancelable(false);
        dialog.show();

        API api = new API();
        Call<List<RunInfo>> call = api.getRetrofitService().GetRunInfoPrev(ID, key);
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
                    RunInfoAdapter runInfoAdapter = new RunInfoAdapter(PrevRunInfoActivity.this, response.body());
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

    private void Search() {
        if(searchET.getText().toString().length()==0)
            key=null;
        else key=searchET.getText().toString();
        GetPrevRunInfo();
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    //좌측 메뉴 열기
    public static void openDrawer() {
        if(!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }

    public static void closeDrawer() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }
}
