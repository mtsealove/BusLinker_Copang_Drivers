package mtsealove.com.github.BuslinkerDrivers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import mtsealove.com.github.BuslinkerDrivers.Design.LoadAdapter;
import mtsealove.com.github.BuslinkerDrivers.Entity.Load;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class RunInfoActivity extends AppCompatActivity {
    final int RequestDriverSet = 300;
    int RunInfoID;
    private TextView startAddrTV, startTimeTV, endAddrTV, endTimeTV, costTV, detailTV, setDriverTV;
    RecyclerView contentView;
    RecyclerView.LayoutManager layoutManager;
    private Button setDriverBtn;
    ScrollView scrollView;
    private int cat;
    private String ID;
    private String DriverID = null;
    private ArrayList<Load> loads;

    //컨텐츠
    LoadAdapter adapter;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_info);

        costTV = findViewById(R.id.costTV);
        detailTV = findViewById(R.id.detailTV);
        setDriverTV = findViewById(R.id.setDriverTV);

        contentView=findViewById(R.id.contentView);
        contentView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        contentView.setLayoutManager(layoutManager);

        setDriverBtn = findViewById(R.id.setDriverBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("불러오는 중입니다");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Intent intent = getIntent();
        RunInfoID = intent.getIntExtra("RunInfoID", 0);
        ID = intent.getStringExtra("CompanyID");
        cat = intent.getIntExtra("cat", 0);
        if (cat == 0)
            setDriverBtn.setVisibility(View.GONE);

        ConnectSocket();


        //기사 선택
        setDriverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(RunInfoActivity.this, SetDriverActivity.class);
                intent1.putExtra("CompanyID", ID);  //회사 ID 전달
                startActivityForResult(intent1, RequestDriverSet);
            }
        });
    }

    //소켓
    private Socket mSocket;

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            JSONObject data = new JSONObject();
            try {
                data.put("RunInfoID", RunInfoID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mSocket.emit("GetRunInfoByID", data);
        }
    };

    String TAG = "결과";
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            try {
                loads=new ArrayList<>();
                Log.e(TAG, args[0].toString());
                JSONObject object = (JSONObject) args[0];

                final String startName=object.getString("startName");
                final String startAddr = object.getString("startAddr");
                final String startTime = (object.getString("startTime")).substring(0, 5) + " 출발";
                final String endName=object.getString("endName");
                final String endAddr = object.getString("endAddr");
                final String endTime = (object.getString("endTime")).substring(0, 5) + "도착 예정";
                //경유지
                final String[] wayloadNames=(object.getString("wayloadNames")).split(";;");
                final String[] wayloadCats = (object.getString("wayloadCats")).split(";;");
                final String[] wayloadAddrs = (object.getString("wayloadAddrs")).split(";;");

                final int cost = object.getInt("charge");

                //출발지 먼저 추가
                loads.add(new Load(RunInfoActivity.this, "출발",startName, startAddr, startTime));
                for (int i = 0; i < wayloadCats.length; i++) {
                    loads.add(new Load(RunInfoActivity.this, wayloadNames[i], wayloadCats[i], wayloadAddrs[i], null));
                }
                loads.add(new Load(RunInfoActivity.this, "도착",endName, endAddr, endTime));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //텍스트 표시
                        costTV.setText(cost + "원");
                        //어댑터 생성 및 적용
                        adapter=new LoadAdapter(RunInfoActivity.this, loads);
                        contentView.setAdapter(adapter);
                    }
                });

            } catch (Exception e) {
                Log.e("Err", e.toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RunInfoActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                mSocket.disconnect();
                progressDialog.dismiss();
            }
        }
    };

    private void ConnectSocket() {
        Log.e("운행정보 상세", "실행");
        try {
            mSocket = IO.socket(LoginActivity.IP);   //서버 주소
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("RunInfoByID", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestDriverSet:
                    String driverID = data.getStringExtra("DriverID");
                    String driverName = data.getStringExtra("DriverName");
                    this.DriverID = driverID; // 기사 ID 설정
                    setDriverTV.setText(driverName + " 기사님");
                    break;
            }
        }
    }
}
