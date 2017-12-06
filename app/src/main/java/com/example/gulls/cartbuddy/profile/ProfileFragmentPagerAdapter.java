package com.example.gulls.cartbuddy.profile;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ben on 12/3/2017.
 */

public class ProfileFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public ProfileFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return UserDealsFragment.newInstance();

            case 1:
                return UserSettingsFragment.newInstance();
        }
        return UserDealsFragment.newInstance();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return UserDealsFragment.TITLE;

            case 1:
                return UserSettingsFragment.TITLE;
        }
        return "";
    }
}
