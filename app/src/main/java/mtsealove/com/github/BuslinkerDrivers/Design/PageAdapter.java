package mtsealove.com.github.BuslinkerDrivers.Design;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import mtsealove.com.github.BuslinkerDrivers.Accounts.FindIdFragment;
import mtsealove.com.github.BuslinkerDrivers.Accounts.FindPwFragment;

public class PageAdapter extends FragmentPagerAdapter {
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0: return FindIdFragment.newInstance();
            case 1: return FindPwFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return "ID 찾기";
            case 1: return "PW 찾기";
            default: return null;
        }
    }
}
