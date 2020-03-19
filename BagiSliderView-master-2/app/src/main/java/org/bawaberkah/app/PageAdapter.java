package org.bawaberkah.app;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;


    public PageAdapter(FragmentManager fm, int NoofTabs) {
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        //Bundle bundle = new Bundle();
        //bundle.putString("my_key", "Test dari Pager");

        switch (position) {
            case 0:
                FragmentSinopsis home = new FragmentSinopsis();
                //home.setArguments(bundle);
                return home;
            case 1:
                FragmentUpdate about = new FragmentUpdate();
                return about;
            case 2:
                FragmentDonatur contact = new FragmentDonatur();
                return contact;
            default:
                return null;
        }
    }
}
