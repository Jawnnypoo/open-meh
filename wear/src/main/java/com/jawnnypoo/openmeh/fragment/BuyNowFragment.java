package com.jawnnypoo.openmeh.fragment;

import android.os.Bundle;
import android.support.wearable.view.ActionPage;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;

import org.parceler.Parcels;

/**
 * Basically just an {@link ActionPage} to show the deal on the phone
 */
public class BuyNowFragment extends ActionPageFragment {

    private static final String ARG_MEH_RESPONSE = "meh_response";

    public static BuyNowFragment newInstance(TinyMehResponse mehResponse) {
        BuyNowFragment fragment = new BuyNowFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MEH_RESPONSE, Parcels.wrap(mehResponse));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setTheThings(ActionPage actionPage) {
        actionPage.setText(getString(R.string.buy));
        actionPage.setImageResource(R.drawable.ic_buy_now_24dp);
    }

    @Override
    protected Theme getTheme() {
        TinyMehResponse response = Parcels.unwrap(getArguments().getParcelable(ARG_MEH_RESPONSE));
        return response.getTheme();
    }

    @Override
    protected void onActionClicked() {
        //TODO
    }
}
