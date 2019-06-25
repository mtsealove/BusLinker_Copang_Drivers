package mtsealove.com.github.BuslinkerDrivers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Design.RunInfoAdapter;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.Entity.RunInfo;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RunInfo> runInfos;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    EditText searchET;
    private String ID, Name;
    private int cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchET = findViewById(R.id.searchET);
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        ConnectSocket();
                        break;
                }
                return false;
            }
        });

        //뷰 초기화
        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //상태바
        SystemUiTuner sut=new SystemUiTuner(this);
        sut.setStatusBarWhite();

        //이전 액티비티로부터 데이터 수신
        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        Log.e("회사ID", ID);
        Name = intent.getStringExtra("Name");
        cat = intent.getIntExtra("cat", 0);

        ConnectSocket();    //소켓 생성

    }

    //소켓
    private Socket mSocket;

    private Emitter.Listener onConnectCompany = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String key = searchET.getText().toString();   //검색어

            JSONObject data = new JSONObject();
            try {
                data.put("Company", ID);
                if (key.length() == 0)
                    data.put("key", null);
                else
                    data.put("key", key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mSocket.emit("GetRunInfo", data);
        }
    };
    private Emitter.Listener onConnectDriver = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String key = searchET.getText().toString();   //검색어

            JSONObject data = new JSONObject();
            try {
                data.put("DriverID", ID);
                if (key.length() == 0)
                    data.put("key", null);
                else
                    data.put("key", key);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mSocket.emit("GetRunInfo", data);
        }
    };

    //데이터 수신 후 이벤트
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runInfos = new ArrayList<>(); //어댑터용

            try {
                JSONArray receivedData = (JSONArray) args[0];
                Log.e("운행정보", receivedData.toString());
                for (int i = 0; i < receivedData.length(); i++) {
                    JSONObject object = receivedData.getJSONObject(i);
                    String startAddr = object.getString("startAddr");
                    String startTime = (object.getString("startTime")).substring(0, 5) + " 출발";
                    String endAddr = object.getString("endAddr");
                    String endTime = (object.getString("endTime")).substring(0, 5) + "도착 예정";
                    String ContractStart=(object.getString("ContractStart")).substring(0, 10);
                    String ContractEnd=(object.getString("ContractEnd")).substring(0,10);
                    String RunDate=null;
                    try{
                        RunDate=(object.getString("RunDate")).substring(0, 10);
                    } catch (Exception e){
                    }

                    int wayloadCnt = ((object.getString("wayloadAddrs")).split(";;")).length;
                    int cost = object.getInt("charge");
                    int infoID = object.getInt("ID");

                    if(RunDate!=null)
                        runInfos.add(new RunInfo(startAddr, startTime, endAddr, endTime, wayloadCnt, cost, infoID, RunDate));
                    else
                        runInfos.add(new RunInfo(startAddr, startTime, endAddr, endTime, wayloadCnt, cost, infoID, ContractStart+"~"+ContractEnd));
                }

                adapter = new RunInfoAdapter(MainActivity.this, runInfos);
                ((RunInfoAdapter) adapter).setCompanyID(ID);
                ((RunInfoAdapter)adapter).setCat(cat);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });

            } catch (Exception e) {
                Log.e("Err", e.toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                mSocket.disconnect();
            }
        }
    };


    private void ConnectSocket() {
        Log.e("운행정보", "실행");
        try {
            mSocket = IO.socket(SetIPActivity.IP);   //서버 주소
            mSocket.connect();
            if (cat == 1) { //업체 회원
                mSocket.on(Socket.EVENT_CONNECT, onConnectCompany);
            } else if (cat == 0) { //개인 회원
                mSocket.on(Socket.EVENT_CONNECT, onConnectDriver);
            }
            mSocket.on("RunInfo", onMessageReceived);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
