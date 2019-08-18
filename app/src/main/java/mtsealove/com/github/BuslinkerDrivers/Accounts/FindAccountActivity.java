package mtsealove.com.github.BuslinkerDrivers.Accounts;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.Design.PageAdapter;
import mtsealove.com.github.BuslinkerDrivers.R;

public class FindAccountActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_account);

        PageAdapter pageAdapter=new PageAdapter(getSupportFragmentManager());
        viewPager=findViewById(R.id.viewPager);
        tabs=findViewById(R.id.tabs);
        viewPager.setAdapter(pageAdapter);
        tabs.setupWithViewPager(viewPager);

        InitTab();

    }

    private void InitTab() {
        TextView tab0=new TextView(this);
        tab0.setText("ID 찾기");
        tab0.setGravity(Gravity.CENTER);
        tab0.setTextColor(Color.BLACK);
        tab0.setTextSize(20);
        TextView tab1=new TextView(this);
        tab1.setText("PW 찾기");
        tab1.setGravity(Gravity.CENTER);
        tab1.setTextColor(Color.BLACK);
        tab1.setTextSize(20);

        tabs.getTabAt(0).setCustomView(tab0);
        tabs.getTabAt(1).setCustomView(tab1);
    }
}
