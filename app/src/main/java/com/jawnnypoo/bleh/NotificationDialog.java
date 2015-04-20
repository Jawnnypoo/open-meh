package com.jawnnypoo.bleh;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class NotificationDialog extends DialogFragment {

    public static String TAG = "NotificationDialog";

    @InjectView(R.id.notification_switch)
    SwitchCompat onOffSwitch;
    @InjectView(R.id.notification_time)
    TextView notifyTime;
    @InjectView(R.id.notification_sound)
    CheckBox soundCheck;
    @InjectView(R.id.notification_tone)
    TextView tone;
    @InjectView(R.id.notification_vibrate)
    CheckBox vibrateCheck;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View v = factory.inflate(R.layout.dialog_notifications, null);
        ButterKnife.inject(this, v);

        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(v);
        return dialog;
    }
}