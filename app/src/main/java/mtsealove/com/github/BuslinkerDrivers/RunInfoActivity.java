package mtsealove.com.github.BuslinkerDrivers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import mtsealove.com.github.BuslinkerDrivers.Design.LoadAdapter;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.Entity.Load;
import mtsealove.com.github.BuslinkerDrivers.Restful.RunInfo;
import org.json.JSONObject;

import java.net.SocketImpl;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RunInfoActivity extends AppCompatActivity {
    final int RequestDriverSet = 300;
    private TextView costTV, setDriverTV, setDateTV;
    private LinearLayout setDriverLayout;
    public static DrawerLayout drawerLayout;
    RecyclerView contentView;
    RecyclerView.LayoutManager layoutManager;
    private Button setDriverBtn;
    private int cat;

    //컨텐츠
    LoadAdapter adapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_info);

        drawerLayout=findViewById(R.id.drawerLayout);
        costTV = findViewById(R.id.costTV);
        setDriverTV = findViewById(R.id.setDriverTV);
        setDateTV = findViewById(R.id.setDateTV);

        contentView = findViewById(R.id.contentView);
        contentView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        contentView.setLayoutManager(layoutManager);

        setDriverLayout=findViewById(R.id.setDriverLayout);
        setDriverBtn = findViewById(R.id.setDriverBtn);

        //하얀 상태바
        SystemUiTuner sut = new SystemUiTuner(this);
        sut.setStatusBarWhite();

        ShowWayLoads();
        //버스 기사일 경우 레이아웃 감추기
        if (cat == 0)
            setDriverLayout.setVisibility(View.GONE);
        else {
            setDriverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //  MakeRunInfoEach();
                }
            });
        }

        //기사 선택
        setDriverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RunDate == null)
                    Toast.makeText(RunInfoActivity.this, "운행 일자를 선택하세요", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent1 = new Intent(RunInfoActivity.this, SetDriverActivity.class);
                    //intent1.putExtra("CompanyID", ID);  //회사 ID 전달
                    intent1.putExtra("RunDate", RunDate);   //운행일자 전달
                    startActivityForResult(intent1, RequestDriverSet);
                }
            }
        });

        setDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRunDate();
            }
        });
    }

    //운행정보 표시
    private void ShowWayLoads() {
        //운행정보 받아오기
        Intent intent = getIntent();
        RunInfo runInfo= (RunInfo) intent.getSerializableExtra("RunInfo");
        //경유지를 분리
        String[] wayLoadNames=runInfo.getWayloadNames().split(";;");
        String[] wayLoadAddrs=runInfo.getWayloadAddrs().split(";;");
        String[] wayLoadCats=runInfo.getWayloadCats().split(";;");
        //어댑터를 통해 리스트에 추가
        ArrayList<Load> loads=new ArrayList<>();
        loads.add(new Load(this, "출발",runInfo.getStartName(), runInfo.getStartAddr(), runInfo.getStartTime()));
        for(int i=0; i<runInfo.getWayloadCnt(); i++){
            loads.add(new Load(this, wayLoadNames[i], wayLoadCats[i], wayLoadAddrs[i], null));
        }
        loads.add(new Load(this, "도착", runInfo.getEndName(), runInfo.getEndAddr(), runInfo.getEndTime()));
        //리스트를 화면에 표시
        LoadAdapter adapter=new LoadAdapter(this, loads);
        contentView.setAdapter(adapter);
        //가격 표시
        costTV.setText(runInfo.getCharge()+"원");
    }

    //운행일 초기화
    private void initRunDate(){
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat Fyear = new SimpleDateFormat("yyyy");
        SimpleDateFormat Fmonth = new SimpleDateFormat("MM");
        SimpleDateFormat Fdate = new SimpleDateFormat("dd");

        //운행일자의 초기값은 오늘
        year = Integer.parseInt(Fyear.format(today));
        month = Integer.parseInt(Fmonth.format(today))-1;
        date = Integer.parseInt(Fdate.format(today));
        RunDate = year + "-" + month + "-" + date;
    }

    //날짜 선택 다이얼로그 출력
    private String RunDate;
    private int year, month, date;
    private void setRunDate() {
        initRunDate();

        DatePickerDialog datePickerDialog=new DatePickerDialog(this, dateSetListener, year, month, date);
        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            RunDate=i+"-"+(i1+1)+"-"+i2;
            setDateTV.setText(RunDate);
        }
    };

    //소켓
    private Socket mSocket;

    /*
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
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @SuppressLint("MissingPermission")
        @Override
        public void call(Object... args) {
            //데이터 파싱
            try {
                loads = new ArrayList<>();
                Log.e(TAG, args[0].toString());
                JSONObject object = (JSONObject) args[0];

                final String startName = object.getString("startName");
                final String startAddr = object.getString("startAddr");
                final String startTime = (object.getString("startTime")).substring(0, 5) + " 출발";
                final String endName = object.getString("endName");
                final String endAddr = object.getString("endAddr");
                final String endTime = (object.getString("endTime")).substring(0, 5) + "도착 예정";
                //경유지
                final String[] wayloadNames = (object.getString("wayloadNames")).split(";;");
                final String[] wayloadCats = (object.getString("wayloadCats")).split(";;");
                final String[] wayloadAddrs = (object.getString("wayloadAddrs")).split(";;");

                final int cost = object.getInt("charge");

                //출발지 먼저 추가
                loads.add(new Load(RunInfoActivity.this, "출발", startName, startAddr, startTime));
                for (int i = 0; i < wayloadCats.length; i++) {
                    loads.add(new Load(RunInfoActivity.this, wayloadNames[i], wayloadCats[i], wayloadAddrs[i], null));
                }
                loads.add(new Load(RunInfoActivity.this, "도착", endName, endAddr, endTime));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                costTV.setText(cost + "원");
                                //텍스트 표시
                                //어댑터 생성 및 적용
                                adapter = new LoadAdapter(RunInfoActivity.this, loads);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RequestDriverSet:  //기사를 설정한 뒤 발생
                    String driverID = data.getStringExtra("DriverID");
                    String driverName = data.getStringExtra("DriverName");
                    this.DriverID = driverID; // 기사 ID 설정
                    setDriverTV.setText(driverName + " 기사님");
                    break;
            }
        }
    }

    //각 운행정보 생성
    private void MakeRunInfoEach() {
        if (DriverID == null) { //기사 ID가 설정되지 않은 경우
            Toast.makeText(this, "기사님을 먼저 선택해 주세요", Toast.LENGTH_SHORT).show();
        } else {    //기사를 선택한 경우
            try {
                //소켓 연결
                progressDialog=new ProgressDialog(this);
                progressDialog.setMessage("기사님을 등록중입니다");
                progressDialog.setCancelable(false);
                progressDialog.show();
                mSocket = IO.socket(SetIPActivity.IP);
                mSocket.connect();
                Log.e("RunInfoSocket", "연결 시도");
                mSocket.on(Socket.EVENT_CONNECT, onRunInfoConnect);
                mSocket.on("RunInfoEach", onRunInfoResultReceived);
            } catch (URISyntaxException e) {
                progressDialog.dismiss();
                e.printStackTrace();
            }
        }
    }

    //운행정보 삽입 소켓 연결 시
    private Emitter.Listener onRunInfoConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = new JSONObject();
            try {
                data.put("RunInfoID", RunInfoID);
                data.put("DriverID", DriverID);
                data.put("RunDate", RunDate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket.emit("CreateRunInfoEach", data);
            Log.e("RunInfoEach", "소켓 연결됬음");
        }
    };

    /*
    //운행정보 삽입 결과 반환
    private Emitter.Listener onRunInfoResultReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //데이터 파싱
            try {
                Log.e("RunInfoEach", "결과 받아옴");
                Log.e(TAG, args[0].toString());
                JSONObject object = (JSONObject) args[0];
                String result = object.getString("Result");
                if (result.equals("OK")) {   //성공 시
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initRunDate();
                            setDateTV.setText("운행일");
                            setDriverTV.setText("기사님 선택");
                            DriverID=null;
                            Toast.makeText(RunInfoActivity.this, "정상적으로 등록되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {    //실패 시
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RunInfoActivity.this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("Err", e.toString());
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RunInfoActivity.this, "데이터 삽입 실패", Toast.LENGTH_SHORT).show();
                    }
                });

            } finally {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
                mSocket.disconnect();
            }
        }
    };

     */

    public static void openDrawer(){
        if(!drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.openDrawer(Gravity.START);
    }

    public static void closeDrawer() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(Gravity.START))
            drawerLayout.closeDrawer(Gravity.START);
        else
            super.onBackPressed();
    }
}