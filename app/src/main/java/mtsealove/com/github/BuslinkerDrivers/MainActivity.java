package mtsealove.com.github.BuslinkerDrivers;

import android.content.Intent;
import android.nfc.Tag;
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
import mtsealove.com.github.BuslinkerDrivers.Design.RecyleAdater;
import mtsealove.com.github.BuslinkerDrivers.Entity.RunInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<RunInfo> runInfos;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    EditText searchET;

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

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ConnectSocket();    //소켓 생성
    }

    //소켓
    private Socket mSocket;

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            String key = searchET.getText().toString();   //검색어
            JSONObject data = new JSONObject();
            try {
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

    String TAG = "결과";
    // 서버로부터 전달받은 'chat-message' Event 처리.
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runInfos = new ArrayList<>(); //어댑터용
            // 전달받은 데이터는 아래와 같이 추출할 수 있습니다.
            try {
                JSONArray receivedData = (JSONArray) args[0];
                for (int i = 0; i < receivedData.length(); i++) {
                    JSONObject object = receivedData.getJSONObject(i);
                    String startAddr = object.getString("startAddr");
                    String startTime = ((object.getString("startTime")).substring(5, 10) + " " + (object.getString("startTime")).substring(11, 16)).replace('-', '/') + " 출발";
                    String endAddr = object.getString("endAddr");
                    String endTime = ((object.getString("endTime")).substring(5, 10) + " " + (object.getString("endTime")).substring(11, 16)).replace('-', '/') + "도착 예정";
                    int wayloadCnt = ((object.getString("wayloadAddrs")).split(";;")).length;
                    int cost = object.getInt("charge");

                    runInfos.add(new RunInfo(startAddr, startTime, endAddr, endTime, wayloadCnt, cost));
                }

                adapter = new RecyleAdater(MainActivity.this, runInfos);
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
            mSocket = IO.socket("http://192.168.10.31:3000");   //서버 주소
            mSocket.connect();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on("RunInfo", onMessageReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
