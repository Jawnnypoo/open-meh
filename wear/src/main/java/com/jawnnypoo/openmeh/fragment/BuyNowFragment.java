package com.jawnnypoo.openmeh.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.ConfirmationActivity;
import android.support.wearable.view.ActionPage;
import android.widget.Toast;

import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.shared.communication.MessageType;
import com.jawnnypoo.openmeh.shared.communication.TinyMehResponse;
import com.jawnnypoo.openmeh.shared.model.Theme;
import com.jawnnypoo.openmeh.util.MessageSender;

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

    MessageSender mMessageSender;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null) {
            if (getParentFragment() instanceof MessageSender) {
                mMessageSender = (MessageSender) getParentFragment();
                return;
            }
        } else if (context instanceof MessageSender) {
            mMessageSender = (MessageSender) context;
            return;
        }
        throw new IllegalStateException(getClass().getSimpleName() + " must be attached to a parent that implements " + MessageSender.class.getSimpleName());
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
        boolean sentMessage = mMessageSender.sendMessage(MessageType.TYPE_BUY_ON_PHONE, null);
        if (sentMessage) {
            Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, getString(R.string.complete_on_phone));
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), R.string.unable_to_connect_to_phone, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
