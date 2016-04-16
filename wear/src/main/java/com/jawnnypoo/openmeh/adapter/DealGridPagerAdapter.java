package com.jawnnypoo.openmeh.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

/**
 * Holds the pages of the meh deal (aka the details, the buy now, the open on phone)
 */
public class DealGridPagerAdapter extends FragmentGridPagerAdapter {

    public DealGridPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getColumnCount(int row) {
        return 3;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public Fragment getFragment(int row, int column) {
        switch (column) {
            case 0:
                return null;
        }
        return null;
    }
}
