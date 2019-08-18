package mtsealove.com.github.BuslinkerDrivers.View;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.Accounts.AccountActivity;
import mtsealove.com.github.BuslinkerDrivers.Accounts.LoginActivity;
import mtsealove.com.github.BuslinkerDrivers.MainActivity;
import mtsealove.com.github.BuslinkerDrivers.PrevRunInfoActivity;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.Restful.API;
import mtsealove.com.github.BuslinkerDrivers.Restful.Count;
import mtsealove.com.github.BuslinkerDrivers.RunInfoActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//사이드 뷰 정의 클래스
public class AccountView extends LinearLayout {
    private TextView NameTV, RecentRunTV, catTV;
    private LinearLayout AccountBtn, RunLogBtn, LogoutBtn;
    private ImageView closeBtn;
    private Context context;
    private String ParentActivityName;

    private static String UserID;
    private static String UserName;
    private String password;

    public AccountView(Context context){
        super(context);
        this.context=context;
        init();
    }

    public AccountView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        this.context=context;
        init();
    }

    public AccountView(Context context, AttributeSet attributeSet, int defStyleAttr){
        super(context, attributeSet, defStyleAttr);
        this.context=context;
        init();
    }
    @RequiresApi(api= Build.VERSION_CODES.LOLLIPOP)
    public AccountView(Context context, AttributeSet attributeSet, int defStyleAttr, int defStyleRes){
        super(context, attributeSet, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init(){
        String inflaterServiice=Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater inflater=(LayoutInflater)getContext().getSystemService(inflaterServiice);
        View view=inflater.inflate(R.layout.view_account, AccountView.this, false);
        //뷰 매칭
        NameTV=view.findViewById(R.id.nameTV);
        RecentRunTV=view.findViewById(R.id.recentDeliveryTV);
        AccountBtn=view.findViewById(R.id.AccountBtn);
        RunLogBtn=view.findViewById(R.id.RunLogBtn);
        LogoutBtn= view.findViewById(R.id.LogoutBtn);
        catTV=view.findViewById(R.id.catTV);
        closeBtn=view.findViewById(R.id.closeBtn);

        LogoutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });
        RunLogBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                RunLog();
            }
        });
        AccountBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Account();
            }
        });
        addView(view);
        ParentActivityName=context.getClass().getSimpleName();
        closeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
            }
        });
    }

    //이름 설정
    public void setName(String name, int cat){
        NameTV.setText(name);
        if(cat==0)
            catTV.setText("기사님");
        else
            catTV.setText("회원님");
    }

    //사용자 ID 설정
    public void setUserID(String userID){
        this.UserID=userID;
        SetRecentRun();
    }

    public void setUserName(String userName){
        this.UserName=userName;
        NameTV.setText(userName);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //로그아웃
    private void LogOut(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("로그아웃")
                .setMessage("로그아웃하시겠습니까?")
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //사용자 ID 초기화
                UserID=null;
                //로그인 화면으로 이동
                Intent intent=new Intent(context, LoginActivity.class);
                intent.putExtra("Logout", true);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    //이전 운행정보
    private void RunLog(){
        if(ParentActivityName.equals("PrevRunInfoActivity")){
        } else {
            closeDrawer();
            Intent intent=new Intent(context, PrevRunInfoActivity.class);
            intent.putExtra("ID", UserID);
            intent.putExtra("Name", UserName);
            context.startActivity(intent);
        }
    }

    //계정정보
    private void Account() {
        if(ParentActivityName.equals("AccountActivity")){
        } else {
            closeDrawer();
            Intent intent=new Intent(context, AccountActivity.class);
            intent.putExtra("password", password);
            intent.putExtra("ID", UserID);
            context.startActivity(intent);
        }
    }

    //좌측 레이아웃 닫기
    private void closeDrawer() {
        String contextStr=context.getClass().getSimpleName();
        switch (contextStr){
            case "MainActivity":
                MainActivity.closeDrawer();
                break;
            case "RunInfoActivity":
                RunInfoActivity.closeDrawer();
                break;
            case "PrevRunInfoActivity":
                PrevRunInfoActivity.closeDrawer();
                break;
        }
    }

    private void SetRecentRun() {
        API api=new API();
        Call<Count> call=api.getRetrofitService().GetRecentRunCnt(UserID);
        call.enqueue(new Callback<Count>() {
            @Override
            public void onResponse(Call<Count> call, Response<Count> response) {
                if(response.isSuccessful()){
                    int count=response.body().getCount();
                    RecentRunTV.setText(Integer.toString(count));
                }
            }

            @Override
            public void onFailure(Call<Count> call, Throwable t) {

            }
        });
    }
}
