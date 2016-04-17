package com.jawnnypoo.openmeh.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.wearable.view.FragmentGridPagerAdapter;

import com.jawnnypoo.openmeh.fragment.BuyNowFragment;
import com.jawnnypoo.openmeh.fragment.DealFragment;
import com.jawnnypoo.openmeh.fragment.ShowOnPhoneFragment;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;

/**
 * Holds the pages of the meh deal (aka the details, the buy now, the open on phone)
 */
public class DealGridPagerAdapter extends FragmentGridPagerAdapter {

    private TinyMehResponse mTinyMehResponse;

    public DealGridPagerAdapter(FragmentManager fm, TinyMehResponse response) {
        super(fm);
        mTinyMehResponse = response;
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
                return DealFragment.newInstance(mTinyMehResponse);
            case 1:
                return ShowOnPhoneFragment.newInstance(mTinyMehResponse);
            case 2:
                return BuyNowFragment.newInstance(mTinyMehResponse);
        }
        throw new IllegalStateException("Idk wut to do");
    }
}
