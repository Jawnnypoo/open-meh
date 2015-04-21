package com.jawnnypoo.bleh;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jawnnypoo.bleh.data.Theme;
import com.jawnnypoo.bleh.util.MehPreferencesManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class NotificationDialog extends DialogFragment {

    public static String TAG = "NotificationDialog";

    @InjectView(R.id.notification_root)
    View root;
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

    Theme theme;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dialog_notifications, container, false);
        ButterKnife.inject(this, v);
        setupUi();
        applyTheme();
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void setupUi() {
        onOffSwitch.setChecked(MehPreferencesManager.getNotificationsPreference(getActivity()));
        soundCheck.setChecked(MehPreferencesManager.getNotificationSound(getActivity()));
        vibrateCheck.setChecked(MehPreferencesManager.getNotificationVibrate(getActivity()));
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationsPreference(getActivity(), isChecked);
            }
        });
        soundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationSound(getActivity(), isChecked);
            }
        });
        vibrateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationVibrate(getActivity(), isChecked);
            }
        });
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void applyTheme() {
        //TODO apply style
    }

    @OnClick(R.id.notification_switch_root)
    void onOffClick(View view) {
        onOffSwitch.setChecked(!onOffSwitch.isChecked());
    }

    @OnClick(R.id.notification_time_root)
    void onTimeClick(View view) {
        //TODO choose time
    }

    @OnClick(R.id.notification_sound_root)
    void onSoundClick(View view) {
        soundCheck.setChecked(!soundCheck.isChecked());
    }

    @OnClick(R.id.notification_vibrate_root)
    void onVibrateClick(View view) {
        vibrateCheck.setChecked(!vibrateCheck.isChecked());
    }
}