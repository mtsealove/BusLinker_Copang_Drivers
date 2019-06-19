package mtsealove.com.github.BuslinkerDrivers;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import mtsealove.com.github.BuslinkerDrivers.Entity.RunInfo;
import net.daum.mf.map.api.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class RunInfoActivity extends AppCompatActivity {
    final int RequestDriverSet=300;
    int RunInfoID;
    private TextView startAddrTV, startTimeTV, endAddrTV, endTimeTV, costTV, simpleTV, detailTV, setDriverTV;
    private Button setDriverBtn;
    ScrollView scrollView;
    float MaxHeight;
    int MinHeight;
    DisplayMetrics metrics;
    private int cat;
    private String ID;

    private LinearLayout wayloadLayout, DetailLayout;
    MapView mapView;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_info);
        startAddrTV = findViewById(R.id.startAddrTV);
        startTimeTV = findViewById(R.id.startTimeTV);
        endAddrTV = findViewById(R.id.endAddrTV);
        endTimeTV = findViewById(R.id.endTimeTV);
        costTV = findViewById(R.id.costTV);
        simpleTV = findViewById(R.id.simpleTV);
        detailTV = findViewById(R.id.detailTV);
        setDriverTV=findViewById(R.id.setDriverTV);
        scrollView = findViewById(R.id.scrollView);
        wayloadLayout = findViewById(R.id.wayloadLayout);
        DetailLayout = findViewById(R.id.DetailLayout);
        mapView = findViewById(R.id.MapLayout);
        mapView.setZoomLevel(9, true);
        setDriverBtn = findViewById(R.id.setDriverBtn);

        final Intent intent = getIntent();
        RunInfoID = intent.getIntExtra("RunInfoID", 0);
        ID=intent.getStringExtra("CompanyID");
        cat = intent.getIntExtra("cat", 0);
        if (cat == 0)
            setDriverBtn.setVisibility(View.GONE);

        Log.e("ID", ID);

        ConnectSocket();
        geocoder = new Geocoder(this);

        metrics = getApplicationContext().getResources().getDisplayMetrics();//화면 크기
        MinHeight = metrics.heightPixels - 200; //최소 높이

        DetailLayout.setY(MaxHeight);

        //움직임에 따른 상세 뷰 위치 조정
        DetailLayout.setOnTouchListener(DetailTouchListener);

        detailTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaxHeight = simpleTV.getY() + ((View) simpleTV.getParent()).getY();   //최대 높이
                MoveView(DetailLayout, MaxHeight, false);
            }
        });

        setDriverTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(RunInfoActivity.this, SetDriverActivity.class);
                intent1.putExtra("CompanyID", ID);
                startActivityForResult(intent1, RequestDriverSet);
            }
        });
    }

    private boolean Opend = false;
    private ArrayList<Float> Increase = new ArrayList<>();
    View.OnTouchListener DetailTouchListener = new View.OnTouchListener() { //하단 뷰 움직이기
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            ViewGroup.LayoutParams params = DetailLayout.getLayoutParams();
            MaxHeight = simpleTV.getY() + ((View) simpleTV.getParent()).getY();   //최대 높이

            int ActionID = motionEvent.getAction();
            switch (ActionID) {
                case MotionEvent.ACTION_DOWN:   //단순 터치 이벤트
                    break;
                case MotionEvent.ACTION_MOVE:   //움직임 이벤트
                    if (motionEvent.getRawY() > MaxHeight && motionEvent.getRawY() < MinHeight) {
                        DetailLayout.setY(motionEvent.getRawY());
                        params.height = metrics.heightPixels;
                        DetailLayout.setLayoutParams(params);
                        Increase.add(motionEvent.getRawY());
                    }
                    break;
                case MotionEvent.ACTION_UP: //손을 땟을 때
                    if (Increase.size() >= 2) {
                        if (Increase.get(0) > Increase.get(1)) {
                            MoveView(DetailLayout, MaxHeight, false);
                        } else {
                            MoveView(DetailLayout, MinHeight, true);
                        }
                        Increase = new ArrayList<>();  //제거
                    }
                    break;
            }
            return false;
        }
    };

    private void MoveView(View view, float Height, boolean Max) {   //뷰 움직이기
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = metrics.heightPixels;
        view.setLayoutParams(params);
        if (Max) {  //닫기
            Opend = false;
            while (view.getY() < Height) {
                view.setY(view.getY() + 1);
            }
        } else {    //열기
            Opend = true;
            while (view.getY() > Height) {
                view.setY(view.getY() - 1);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (Opend)  //열려있다면 닫기
            MoveView(DetailLayout, MinHeight, true);
        else {
            super.onBackPressed();
        }
    }

    ;

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

                Log.e(TAG, args[0].toString());
                JSONObject object = (JSONObject) args[0];

                final String startAddr = object.getString("startAddr");
                final String startTime = (object.getString("startTime")).substring(0, 5) + " 출발";
                final String endAddr = object.getString("endAddr");
                final String endTime = (object.getString("endTime")).substring(0, 5) + "도착 예정";
                final String[] wayloadCats = (object.getString("wayloadCats")).split(";;");
                final String[] wayloadAddrs = (object.getString("wayloadAddrs")).split(";;");
                final int cost = object.getInt("charge");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //텍스트 표시
                        startAddrTV.setText(startAddr);
                        startTimeTV.setText(startTime);
                        endAddrTV.setText(endAddr);
                        endTimeTV.setText(endTime);
                        costTV.setText(cost + "원");


                        Address StartAddress = null, EndAddress = null;
                        //지도에 표시
                        try {   //출발 도착
                            MapPOIItem startPoint = new MapPOIItem();
                            StartAddress = geocoder.getFromLocationName(startAddr, 1).get(0);
                            startPoint.setItemName(startAddr);
                            startPoint.setTag(0);
                            startPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(StartAddress.getLatitude(), StartAddress.getLongitude()));
                            startPoint.setMarkerType(MapPOIItem.MarkerType.BluePin);
                            startPoint.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                            mapView.addPOIItem(startPoint);
                            EndAddress = geocoder.getFromLocationName(endAddr, 1).get(0);
                            MapPOIItem endPoint = new MapPOIItem();
                            endPoint.setItemName(endAddr);
                            endPoint.setTag(1);
                            endPoint.setMapPoint(MapPoint.mapPointWithGeoCoord(EndAddress.getLatitude(), EndAddress.getLongitude()));
                            endPoint.setMarkerType(MapPOIItem.MarkerType.BluePin);
                            endPoint.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                            mapView.addPOIItem(endPoint);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        MapPolyline polyline = new MapPolyline();
                        polyline.setTag(2000);
                        polyline.setLineColor(getColor(R.color.colorPrimary));
                        polyline.addPoint(MapPoint.mapPointWithGeoCoord(StartAddress.getLatitude(), StartAddress.getLongitude()));  //시작 주소 추가

                        LayoutInflater inflater = getLayoutInflater();
                        for (int i = 0; i < wayloadCats.length; i++) {
                            //텍스트로 표시
                            View view = inflater.inflate(R.layout.adapter_wayloads, null);
                            TextView catTV = view.findViewById(R.id.catTV);
                            TextView addressTV = view.findViewById(R.id.addressTV);
                            TextView showMapTV = view.findViewById(R.id.showMapTV);
                            catTV.setText(wayloadCats[i]);
                            addressTV.setText(wayloadAddrs[i]);
                            wayloadLayout.addView(view);
                            //마커 표시
                            try {
                                Address address = geocoder.getFromLocationName(wayloadAddrs[i], 1).get(0);
                                polyline.addPoint(MapPoint.mapPointWithGeoCoord(address.getLatitude(), address.getLongitude()));
                                MapPOIItem marker = new MapPOIItem();
                                marker.setItemName("주소: " + wayloadAddrs[i] + " 종류: " + wayloadCats[i]);
                                marker.setMapPoint(MapPoint.mapPointWithGeoCoord(address.getLatitude(), address.getLongitude()));
                                marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                                mapView.addPOIItem(marker);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        polyline.addPoint(MapPoint.mapPointWithGeoCoord(EndAddress.getLatitude(), EndAddress.getLongitude()));  //도착 주소 추가
                        mapView.addPolyline(polyline);

                        MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
                        int padding = 100;
                        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case RequestDriverSet:

                    break;

            }
        }
    }
}
