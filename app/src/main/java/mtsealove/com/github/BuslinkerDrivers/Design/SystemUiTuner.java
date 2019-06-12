package mtsealove.com.github.BuslinkerDrivers.Design;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;

//상하단바 수정 클래스
public class SystemUiTuner {
    final private Context context;
    public SystemUiTuner(Context context) {
        this.context=context;
    }

    public void setStatusBarWhite() {   //하얀 배경에 검은 아이콘
        View view=((Activity)context).getWindow().getDecorView();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            if(view!=null) {
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }
}