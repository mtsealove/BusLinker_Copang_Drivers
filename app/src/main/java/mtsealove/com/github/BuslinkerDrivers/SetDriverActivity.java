package mtsealove.com.github.BuslinkerDrivers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import mtsealove.com.github.BuslinkerDrivers.Design.DriverAdapter;
import mtsealove.com.github.BuslinkerDrivers.Entity.Driver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;

public class SetDriverActivity extends AppCompatActivity {
    private String companyID, RunDate;
    RecyclerView contentView;
    List<Driver> drivers;
    DriverAdapter driverAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_driver);
        //리사이클 뷰 초기화
        contentView = findViewById(R.id.contentView);
        contentView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        contentView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        companyID = intent.getStringExtra("CompanyID");    //회사 ID
        RunDate=intent.getStringExtra("RunDate");
        ConnectSocket();

    }

    private Socket socket;

    private void ConnectSocket() {
        try {
            socket = IO.socket(SetIPActivity.IP);
            socket.connect();
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on("FreeDriver", onReceived);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    ProgressDialog progressDialog;
    private Emitter.Listener onConnect = new Emitter.Listener() {    //소켓 연결 성공 시
        @Override
        public void call(Object... args) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(SetDriverActivity.this);
                    progressDialog.setMessage("기사님 불러오는 중");
                    progressDialog.show();
                }
            });
            JSONObject data = new JSONObject();
            try {
                //ID와 운행 날짜를 넘겨줌
                data.put("CompanyID", companyID);
                data.put("date", RunDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            socket.emit("GetFreeDriver", data);
        }
    };

    private Emitter.Listener onReceived = new Emitter.Listener() {    //데이터 수신
        @Override
        public void call(Object... args) {
            drivers = new ArrayList<>();
            try {    //데이터 파싱
                JSONArray data = (JSONArray) args[0];
                for (int i = 0; i < data.length(); i++) {
                    JSONObject object = data.getJSONObject(i);
                    final String Name = object.getString("Name");
                    final String ID = object.getString("ID");
                    final String CarType = object.getString("CarType");
                    final int CarYear = object.getInt("CarYear");
                    final String Contact = object.getString("Contact");

                    drivers.add(new Driver(Name, ID, CarType, CarYear, Contact));
                }
                driverAdapter = new DriverAdapter(SetDriverActivity.this, drivers);
                Log.e("adapter", "생성");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contentView.setAdapter(driverAdapter);

                        contentView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                            @Override
                            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                                View child=recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                                if(child!=null){
                                    int position=recyclerView.getChildAdapterPosition(child);
                                    final Driver driver=drivers.get(position);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            MakeConfirmDialog(driver.getName(), driver.getID());
                                        }
                                    });
                                }
                                return false;
                            }

                            @Override
                            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

                            }

                            @Override
                            public void onRequestDisallowInterceptTouchEvent(boolean b) {

                            }
                        });
                        progressDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //기사님 선택 확인 다이얼로그 생성
    AlertDialog confirmDialog=null;
    private void MakeConfirmDialog(final String DriverName, final String DriverID){
        AlertDialog.Builder builder = new AlertDialog.Builder(SetDriverActivity.this);
        builder.setTitle("선택 확인")
                .setMessage(DriverName + "기사님을 선택하셨습니다.\n계속하시겠습니까?")
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //결과 반환
                Intent resultIntent = new Intent();
                resultIntent.putExtra("DriverID", DriverID);
                resultIntent.putExtra("DriverName", DriverName);
                setResult(RESULT_OK, resultIntent);
                confirmDialog.dismiss();
                ((Activity)SetDriverActivity.this).finish();
            }
        });
        confirmDialog = builder.create();
        confirmDialog.show();
    }

    @Override
    public void onDestroy(){
        Log.e("SetDriver", "onDestroy");
        if(confirmDialog!=null)
            confirmDialog.dismiss();
        super.onDestroy();
    }
    @Override
    public void onStop(){
        Log.e("SetDriver", "onStop");
        super.onStop();
    }
}
