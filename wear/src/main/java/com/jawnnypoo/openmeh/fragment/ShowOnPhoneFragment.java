package com.jawnnypoo.openmeh.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.ActionPage;
import android.widget.Toast;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.model.MehWearResponse;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.model.Theme;

import org.parceler.Parcels;

/**
 * Basically just an {@link android.support.wearable.view.ActionPage} to show the deal on the phone
 */
public class ShowOnPhoneFragment extends ActionPageFragment {

    private static final String ARG_MEH_RESPONSE = "meh_response";

    public static ShowOnPhoneFragment newInstance(MehWearResponse mehResponse) {
        ShowOnPhoneFragment fragment = new ShowOnPhoneFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MEH_RESPONSE, Parcels.wrap(mehResponse));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setTheThings(ActionPage actionPage) {
        actionPage.setText(getString(R.string.view_on_phone));
        actionPage.setImageResource(R.drawable.common_full_open_on_phone);
    }

    @Override
    protected Theme getTheme() {
        MehWearResponse response = Parcels.unwrap(getArguments().getParcelable(ARG_MEH_RESPONSE));
        return response.getTinyMehResponse().getTheme();
    }

    @Override
    protected void onActionClicked() {
        //http://stackoverflow.com/questions/25482930/how-to-implement-open-on-phone-animation-on-android-wear
        boolean sentMessage = getMessageSender().sendMessage(MessageType.TYPE_OPEN_ON_PHONE, null);
        if (sentMessage) {
            Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.unable_to_connect_to_phone, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
