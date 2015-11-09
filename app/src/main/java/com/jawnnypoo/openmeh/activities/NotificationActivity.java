package com.jawnnypoo.openmeh.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.commit451.easel.Easel;
import com.jawnnypoo.openmeh.R;
import com.jawnnypoo.openmeh.data.Theme;
import com.jawnnypoo.openmeh.dialogs.NotifyIfDialog;
import com.jawnnypoo.openmeh.util.MehPreferencesManager;
import com.jawnnypoo.openmeh.util.MehReminderManager;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Notify all the things!
 * Created by Jawn on 4/23/2015.
 */
public class NotificationActivity extends BaseActivity {

    private static final String TIMEPICKER_TAG = "timepicker";
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a");

    public static Intent newInstance(Context context) {
        return newInstance(context, null);
    }

    public static Intent newInstance(Context context, Theme theme) {
        Intent intent = new Intent(context, NotificationActivity.class);
        if (theme != null) {
            intent.putExtra(EXTRA_THEME, Parcels.wrap(theme));
        }
        return intent;
    }

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mToolbarTitle;
    @Bind(R.id.notification_switch) SwitchCompat mOnOffSwitch;
    @Bind(R.id.notification_switch_label) TextView mOnOffSwitchLabel;
    @Bind(R.id.notification_time) TextView mNotifyTime;
    @Bind(R.id.notification_time_label) TextView mNotifyTimeLabel;
    @Bind(R.id.notification_sound) CheckBox mSoundCheck;
    @Bind(R.id.notification_sound_label) TextView mSoundCheckLabel;
    @Bind(R.id.notification_vibrate) CheckBox mVibrateCheck;
    @Bind(R.id.notification_vibrate_label) TextView mVibrateCheckLabel;

    Calendar mTimeToAlert = Calendar.getInstance();
    TimePickerDialog mTimePickerDialog;
    NotifyIfDialog mNotifyIfDialog;

    private final TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            mTimeToAlert.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mTimeToAlert.set(Calendar.MINUTE, minute);
            mNotifyTime.setText(TIME_FORMAT.format(mTimeToAlert.getTime()));
            MehPreferencesManager.setNotificationPreferenceHour(NotificationActivity.this, hourOfDay);
            MehPreferencesManager.setNotificationPreferenceMinute(NotificationActivity.this, minute);
            MehReminderManager.scheduleDailyReminder(NotificationActivity.this, hourOfDay, minute);
            //Recreate for next time, starting with the newly set time
            mTimePickerDialog.setStartTime(hourOfDay, minute);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        ButterKnife.bind(this);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mToolbarTitle.setText(R.string.action_notifications);

        mTimeToAlert.set(Calendar.HOUR_OF_DAY, MehPreferencesManager.getNotificationPreferenceHour(this));
        mTimeToAlert.set(Calendar.MINUTE, MehPreferencesManager.getNotificationPreferenceMinute(this));
        setupUi();
        mNotifyIfDialog = new NotifyIfDialog(this);
        mTimePickerDialog = (TimePickerDialog) getFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
        if (mTimePickerDialog == null) {
            mTimePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, mTimeToAlert.get(Calendar.HOUR_OF_DAY), mTimeToAlert.get(Calendar.MINUTE), false);
            mTimePickerDialog.vibrate(false);
        }
        Theme theme = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_THEME));
        if (theme != null) {
            applyTheme(theme);
        }
    }

    private void applyTheme(Theme theme) {
        //Tint widgets
        int accentColor = theme.getAccentColor();
        int foreGround = theme.getForeground() == Theme.FOREGROUND_LIGHT ? Color.WHITE : Color.BLACK;
        Easel.setTint(mOnOffSwitch, accentColor, foreGround);
        Easel.setTint(mSoundCheck, accentColor);
        Easel.setTint(mVibrateCheck, accentColor);
        mToolbarTitle.setTextColor(theme.getBackgroundColor());
        mToolbar.setBackgroundColor(accentColor);
        mToolbar.getNavigationIcon().setColorFilter(theme.getBackgroundColor(), PorterDuff.Mode.MULTIPLY);
        if (Build.VERSION.SDK_INT >= 21) {
            int darkerAccentColor = Easel.getDarkerColor(accentColor);
            getWindow().setStatusBarColor(darkerAccentColor);
            getWindow().setNavigationBarColor(darkerAccentColor);
        }
        getWindow().getDecorView().setBackgroundColor(theme.getBackgroundColor());
        mOnOffSwitchLabel.setTextColor(foreGround);
        mNotifyTime.setTextColor(foreGround);
        mNotifyTimeLabel.setTextColor(foreGround);
        mSoundCheckLabel.setTextColor(foreGround);
        mVibrateCheckLabel.setTextColor(foreGround);
        mTimePickerDialog.setAccentColor(theme.getAccentColor());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out);
    }

    private void setupUi() {
        mOnOffSwitch.setChecked(MehPreferencesManager.getNotificationsPreference(this));
        mSoundCheck.setChecked(MehPreferencesManager.getNotificationSound(this));
        mVibrateCheck.setChecked(MehPreferencesManager.getNotificationVibrate(this));
        mOnOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationsPreference(NotificationActivity.this, isChecked);
                if (isChecked) {
                    MehReminderManager.restoreReminderPreference(NotificationActivity.this);
                } else {
                    MehReminderManager.cancelPendingReminders(NotificationActivity.this);
                }
            }
        });

        mNotifyTime.setText(TIME_FORMAT.format(mTimeToAlert.getTime()));
        mSoundCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationSound(NotificationActivity.this, isChecked);
            }
        });
        mVibrateCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MehPreferencesManager.setNotificationVibrate(NotificationActivity.this, isChecked);
            }
        });
    }

    @OnClick(R.id.notification_switch_root)
    void onOffClick() {
        mOnOffSwitch.setChecked(!mOnOffSwitch.isChecked());
    }

    @OnClick(R.id.notification_time_root)
    void onTimeClick() {
        mTimePickerDialog.show(getFragmentManager(), TIMEPICKER_TAG);

    }

    @OnClick(R.id.notification_sound_root)
    void onSoundClick() {
        mSoundCheck.setChecked(!mSoundCheck.isChecked());
    }

    @OnClick(R.id.notification_vibrate_root)
    void onVibrateClick() {
        mVibrateCheck.setChecked(!mVibrateCheck.isChecked());
    }

    @OnClick(R.id.notification_key_words_root)
    void onKeyWordsClick() {
        mNotifyIfDialog.show();
    }
}
