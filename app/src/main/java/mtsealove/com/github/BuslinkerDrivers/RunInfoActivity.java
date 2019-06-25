package mtsealove.com.github.BuslinkerDrivers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import mtsealove.com.github.BuslinkerDrivers.Design.LoadAdapter;
import mtsealove.com.github.BuslinkerDrivers.Design.SystemUiTuner;
import mtsealove.com.github.BuslinkerDrivers.Entity.Load;
import org.json.JSONObject;

import java.net.SocketImpl;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RunInfoActivity extends AppCompatActivity {
    final int RequestDriverSet = 300;
    int RunInfoID;
    private TextView costTV, setDriverTV, setDateTV;
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
        setDriverTV = findViewById(R.id.setDriverTV);
        setDateTV = findViewById(R.id.setDateTV);

        contentView = findViewById(R.id.contentView);
        contentView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        contentView.setLayoutManager(layoutManager);

        setDriverBtn = findViewById(R.id.setDriverBtn);

        SystemUiTuner sut = new SystemUiTuner(this);
        sut.setStatusBarWhite();

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
        else {
            setDriverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MakeRunInfoEach();
                }
            });
        }

        ConnectSocket();


        //기사 선택
        setDriverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RunDate == null)
                    Toast.makeText(RunInfoActivity.this, "운행 일자를 선택하세요", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent1 = new Intent(RunInfoActivity.this, SetDriverActivity.class);
                    intent1.putExtra("CompanyID", ID);  //회사 ID 전달
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

    //운행일 초기화
    private void initRunDate(){
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat Fyear = new SimpleDateFormat("yyyy");
        SimpleDateFormat Fmonth = new SimpleDateFormat("MM");
        SimpleDateFormat Fdate = new SimpleDateFormat("dd");

        //운행일자의 초기값은 오늘
        year = Integer.parseInt(Fyear.format(today));
        month = Integer.parseInt(Fmonth.format(today));
        date = Integer.parseInt(Fdate.format(today));
        RunDate = year + "-" + month + "-" + date;
    }

    //날짜 선택 다이얼로그 출력
    private String RunDate;
    private int year, month, date;
    private void setRunDate() {
        initRunDate();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DatePicker datePicker = new DatePicker(this);

        builder.setView(datePicker);
        datePicker.init(year, month-1, date, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = i;
                month = i1 + 1;
                date = i2;
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RunDate = year + "-" + month + "-" + date;
                setDateTV.setText(RunDate);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFffff")));
        dialog.show();
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
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
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
                        //텍스트 표시
                        costTV.setText(cost + "원");
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

    //소켓 생성
    private void ConnectSocket() {
        try {
            mSocket = IO.socket(SetIPActivity.IP);   //서버 주소
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
}