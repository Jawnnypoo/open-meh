package com.jawnnypoo.openmeh;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jawnnypoo.openmeh.util.MehPreferencesManager;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class NotificationDialog extends AppCompatDialog {

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

    public NotificationDialog(Context context) {
        super(context);
    }

    public NotificationDialog(Context context, int theme) {
        super(context, theme);
    }

    public NotificationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_notifications);
        ButterKnife.inject(this);
        setupUi();
    }

    private void setupUi() {
        onOffSwitch.setChecked(MehPreferencesManager.getNotificationsPreference(getContext()));
        soundCheck.setChecked(MehPreferencesManager.getNotificationSound(getContext()));
        vibrateCheck.setChecked(MehPreferencesManager.getNotificationVibrate(getContext()));
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationsPreference(getContext(), isChecked);
            }
        });
        soundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationSound(getContext(), isChecked);
            }
        });
        vibrateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationVibrate(getContext(), isChecked);
            }
        });
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