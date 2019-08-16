package mtsealove.com.github.BuslinkerDrivers.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import mtsealove.com.github.BuslinkerDrivers.MainActivity;
import mtsealove.com.github.BuslinkerDrivers.PrevRunInfoActivity;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.RunInfoActivity;

public class TitleView extends RelativeLayout {
    private Context context;
    ImageView panelBtn;

    public TitleView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.title_view, null);
        panelBtn = view.findViewById(R.id.panelBtn);
        panelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        addView(view);
    }

    private void openDrawer() {
        String contextName = context.getClass().getSimpleName();
        switch (contextName) {
            case "MainActivity":
                MainActivity.openDrawer();
                break;
            case "PrevRunInfoActivity":
                PrevRunInfoActivity.openDrawer();
                break;
            case "RunInfoActivity":
                RunInfoActivity.openDrawer();
                break;
        }
    }
}