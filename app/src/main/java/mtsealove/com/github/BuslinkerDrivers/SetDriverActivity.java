package mtsealove.com.github.BuslinkerDrivers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedMap;

public class SetDriverActivity extends AppCompatActivity {
    String companyID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_driver);

        Intent intent=getIntent();
        companyID=intent.getStringExtra("CompanyID");    //회사 ID
        ConnectSocket();

    }

    private Socket socket;

    private void ConnectSocket() {
    try{
        socket= IO.socket(LoginActivity.IP);
        socket.connect();
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on("FreeDriver", onReceived);
    } catch (URISyntaxException e) {
        e.printStackTrace();
    }
    }

    ProgressDialog progressDialog;
    private Emitter.Listener onConnect =new Emitter.Listener() {    //소켓 연결 성공 시
        @Override
        public void call(Object... args) {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog=new ProgressDialog(SetDriverActivity.this);
                    progressDialog.setMessage("기사님 불러오는 중");
                    progressDialog.show();
                }
            });
            JSONObject data=new JSONObject();
            try{
                //ID와 현재 날짜를 넘겨줌
                data.put("CompanyID", companyID);
                data.put("date", dateFormat.format(new Date()));
            }catch (Exception e){
                e.printStackTrace();
            }
            socket.emit("GetFreeDriver", data);
        }
    };

    private Emitter.Listener onReceived=new Emitter.Listener() {    //데이터 수신
        @Override
        public void call(Object... args) {
            try{    //데이터 파싱
                JSONArray data=(JSONArray)args[0];
                for(int i=0; i<data.length(); i++){
                    JSONObject object=data.getJSONObject(i);
                    final String Name=object.getString("Name");
                    final String ID=object.getString("ID");
                    final String CarType=object.getString("CarType");
                    final int CarYear=object.getInt("CarYear");
                    final String Contact=object.getString("Contact");

                    Log.e("기사정보", Name+" "+ID+" "+CarType+" "+CarYear+" "+Contact);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AddDriverView(Name, ID, CarType, CarYear, Contact);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    };

    private void AddDriverView(String Name, String ID, String CarType, int CarYear, String Contact){
        LinearLayout driverLayout=findViewById(R.id.driverLayout);
        //레이아웃 inflate
        LayoutInflater inflater=getLayoutInflater();
        View adapter=inflater.inflate(R.layout.adapter_drivers, null);
        TextView NameTV=adapter.findViewById(R.id.nameTV);
        TextView IDTV=adapter.findViewById(R.id.IDTV);
        TextView CarTypeTV=adapter.findViewById(R.id.carTypeTV);
        TextView CarYearTV=adapter.findViewById(R.id.carYearTV);
        TextView ContactTV=adapter.findViewById(R.id.contactTV);

        //화면에 표시
        NameTV.setText(Name);
        IDTV.setText(ID);
        CarTypeTV.setText(CarType);
        CarYearTV.setText(Integer.toString(CarYear));
        ContactTV.setText(Contact);
        driverLayout.addView(adapter);
    }
}
