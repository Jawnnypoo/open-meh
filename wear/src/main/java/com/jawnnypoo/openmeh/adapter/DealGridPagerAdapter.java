package com.jawnnypoo.openmeh.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.jawnnypoo.openmeh.fragment.BuyNowFragment;
import com.jawnnypoo.openmeh.fragment.DealFragment;
import com.jawnnypoo.openmeh.fragment.ShowOnPhoneFragment;
import com.jawnnypoo.openmeh.model.MehWearResponse;

/**
 * Holds the pages of the meh deal (aka the details, the buy now, the open on phone)
 */
public class DealGridPagerAdapter extends FragmentGridPagerAdapter {

    private MehWearResponse mResponse;

    public DealGridPagerAdapter(FragmentManager fm, MehWearResponse response) {
        super(fm);
        mResponse = response;
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
                return DealFragment.newInstance(mResponse);
            case 1:
                return ShowOnPhoneFragment.newInstance(mResponse);
            case 2:
                return BuyNowFragment.newInstance(mResponse);
        }
        throw new IllegalStateException("Idk wut to do");
    }
}
