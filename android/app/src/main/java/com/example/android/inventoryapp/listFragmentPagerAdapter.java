package com.example.android.inventoryapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Provides the appropriate {@link Fragment} for a view pager.
 */
public class listFragmentPagerAdapter extends FragmentPagerAdapter {
    String parentName;

    private String tabTitles[] = new String[] { "InStock", "OutStock", "ShopStock" };

    public listFragmentPagerAdapter(FragmentManager fm, String parentListName) {
        super(fm);
        parentName = parentListName;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("parentList", parentName);
        if (position == 0) {
            bundle.putChar("table", 'i');
            Fragment in = new StockFragment();
            in.setArguments(bundle);
            return in;
        } else if (position == 1){
            bundle.putChar("table", 'o');
            Fragment in = new StockFragment();
            in.setArguments(bundle);
            return in;
        } else {
            bundle.putChar("table", 's');
            Fragment in = new StockFragment();
            in.setArguments(bundle);
            return in;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
