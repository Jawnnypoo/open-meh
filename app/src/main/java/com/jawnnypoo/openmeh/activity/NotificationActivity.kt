package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.commit451.addendum.parceler.getParcelerParcelableExtra
import com.commit451.addendum.parceler.putParcelerParcelableExtra
import com.commit451.easel.Easel
import com.commit451.easel.tint
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.job.ReminderJob
import com.jawnnypoo.openmeh.shared.model.Theme
import com.jawnnypoo.openmeh.util.Prefs
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

/**
 * Notify all the things!
 */
class NotificationActivity : BaseActivity() {

    companion object {

        private val TAG_TIME_PICKER = "timepicker"
        private val TIME_FORMAT = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun newInstance(context: Context, theme: Theme?): Intent {
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putParcelerParcelableExtra(BaseActivity.EXTRA_THEME, theme)
            return intent
        }
    }

    @BindView(R.id.toolbar) lateinit var toolbar: Toolbar
    @BindView(R.id.toolbar_title) lateinit var toolbarTitle: TextView
    @BindView(R.id.notification_switch) lateinit var switchNotifications: SwitchCompat
    @BindView(R.id.notification_switch_label) lateinit var labelNotifications: TextView
    @BindView(R.id.notification_time) lateinit var textNotifyTime: TextView
    @BindView(R.id.notification_time_label) lateinit var labelNotifyTime: TextView
    @BindView(R.id.notification_sound) lateinit var checkBoxSound: CheckBox
    @BindView(R.id.notification_sound_label) lateinit var labelSound: TextView
    @BindView(R.id.notification_vibrate) lateinit var checkBoxVibrate: CheckBox
    @BindView(R.id.notification_vibrate_label) lateinit var labelVibrate: TextView

    var timeToAlert: Calendar = Calendar.getInstance()
    var timePickerDialog: TimePickerDialog? = null

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
        timeToAlert.set(Calendar.HOUR_OF_DAY, hourOfDay)
        timeToAlert.set(Calendar.MINUTE, minute)
        textNotifyTime.text = TIME_FORMAT.format(timeToAlert.time)
        Prefs.setNotificationPreferenceHour(this@NotificationActivity, hourOfDay)
        Prefs.setNotificationPreferenceMinute(this@NotificationActivity, minute)
        ReminderJob.schedule(hourOfDay, minute)
        //Recreate for next time, starting with the newly set time
        timePickerDialog?.setInitialSelection(hourOfDay, minute)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        ButterKnife.bind(this)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbarTitle.setText(R.string.action_notifications)

        timeToAlert.set(Calendar.HOUR_OF_DAY, Prefs.getNotificationPreferenceHour(this))
        timeToAlert.set(Calendar.MINUTE, Prefs.getNotificationPreferenceMinute(this))
        setupUi()
        timePickerDialog = fragmentManager.findFragmentByTag(TAG_TIME_PICKER) as? TimePickerDialog
        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, timeToAlert.get(Calendar.HOUR_OF_DAY), timeToAlert.get(Calendar.MINUTE), false)
            timePickerDialog!!.vibrate(false)
        }
        val theme = intent.getParcelerParcelableExtra<Theme>(BaseActivity.Companion.EXTRA_THEME)
        if (theme != null) {
            applyTheme(theme)
        }
    }

    private fun applyTheme(theme: Theme) {
        //Tint widgets
        val accentColor = theme.safeAccentColor()
        val foreGround = theme.safeForegroundColor()
        switchNotifications.tint(accentColor, foreGround)
        checkBoxSound.tint(accentColor)
        checkBoxVibrate.tint(accentColor)
        toolbarTitle.setTextColor(theme.safeBackgroundColor())
        toolbar.setBackgroundColor(accentColor)
        toolbar.navigationIcon?.setColorFilter(theme.safeBackgroundColor(), PorterDuff.Mode.MULTIPLY)
        if (Build.VERSION.SDK_INT >= 21) {
            val darkerAccentColor = Easel.darkerColor(accentColor)
            window.statusBarColor = darkerAccentColor
            window.navigationBarColor = darkerAccentColor
        }
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
        labelNotifications.setTextColor(foreGround)
        textNotifyTime.setTextColor(foreGround)
        labelNotifyTime.setTextColor(foreGround)
        labelSound.setTextColor(foreGround)
        labelVibrate.setTextColor(foreGround)
        timePickerDialog?.accentColor = theme.safeAccentColor()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out)
    }

    private fun setupUi() {
        switchNotifications.isChecked = Prefs.getNotificationsPreference(this)
        checkBoxSound.isChecked = Prefs.getNotificationSound(this)
        checkBoxVibrate.isChecked = Prefs.getNotificationVibrate(this)
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Prefs.setNotificationsPreference(this@NotificationActivity, isChecked)
            if (isChecked) {
                val hourOfDay = Prefs.getNotificationPreferenceHour(this)
                val minuteOfDay = Prefs.getNotificationPreferenceMinute(this)
                ReminderJob.schedule(hourOfDay, minuteOfDay)
            } else {
                ReminderJob.cancel()
            }
        }

        textNotifyTime.text = TIME_FORMAT.format(timeToAlert.time)
        checkBoxSound.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationSound(this@NotificationActivity, isChecked) }
        checkBoxVibrate.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationVibrate(this@NotificationActivity, isChecked) }
    }

    @OnClick(R.id.notification_switch_root)
    fun onOffClick() {
        switchNotifications.isChecked = !switchNotifications.isChecked
    }

    @OnClick(R.id.notification_time_root)
    fun onTimeClick() {
        timePickerDialog?.show(fragmentManager, TAG_TIME_PICKER)

    }

    @OnClick(R.id.notification_sound_root)
    fun onSoundClick() {
        checkBoxSound.isChecked = !checkBoxSound.isChecked
    }

    @OnClick(R.id.notification_vibrate_root)
    fun onVibrateClick() {
        checkBoxVibrate.isChecked = !checkBoxVibrate.isChecked
    }
}
