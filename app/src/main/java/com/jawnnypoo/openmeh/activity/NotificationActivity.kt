package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import com.commit451.easel.Easel
import com.commit451.easel.tint
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.job.ReminderJob
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.util.Prefs
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_notifications.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Notify all the things!
 */
class NotificationActivity : BaseActivity() {

    companion object {

        private const val TAG_TIME_PICKER = "timepicker"

        fun newInstance(context: Context, theme: ParsedTheme?): Intent {
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putExtra(EXTRA_THEME, theme)
            return intent
        }
    }

    private var timeToAlert: Calendar = Calendar.getInstance()
    private var timePickerDialog: TimePickerDialog? = null

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
        timeToAlert.set(Calendar.HOUR_OF_DAY, hourOfDay)
        timeToAlert.set(Calendar.MINUTE, minute)
        textTime.text = timeFormat().format(timeToAlert.time)
        Prefs.setNotificationPreferenceHour(this@NotificationActivity, hourOfDay)
        Prefs.setNotificationPreferenceMinute(this@NotificationActivity, minute)
        ReminderJob.schedule(hourOfDay, minute)
        //Recreate for next time, starting with the newly set time
        timePickerDialog?.setInitialSelection(hourOfDay, minute)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        textToolbarTitle.setText(R.string.action_notifications)

        timeToAlert.set(Calendar.HOUR_OF_DAY, Prefs.getNotificationPreferenceHour(this))
        timeToAlert.set(Calendar.MINUTE, Prefs.getNotificationPreferenceMinute(this))
        setupUi()
        rootNotifications.setOnClickListener { switchNotifications.toggle() }
        rootNotificationTime.setOnClickListener { timePickerDialog?.show(supportFragmentManager, TAG_TIME_PICKER) }
        rootNotificationSound.setOnClickListener { checkBoxSound.toggle() }
        rootVibrate.setOnClickListener { checkBoxVibrate.toggle() }
        timePickerDialog = supportFragmentManager.findFragmentByTag(TAG_TIME_PICKER) as? TimePickerDialog
        if (timePickerDialog == null) {
            timePickerDialog = TimePickerDialog.newInstance(onTimeSetListener, timeToAlert.get(Calendar.HOUR_OF_DAY), timeToAlert.get(Calendar.MINUTE), false)
            timePickerDialog?.vibrate(false)
        }
        val theme = intent.getParcelableExtra<ParsedTheme>(EXTRA_THEME)
        if (theme != null) {
            applyTheme(theme)
        }
    }

    private fun applyTheme(theme: ParsedTheme) {
        //Tint widgets
        val accentColor = theme.safeAccentColor()
        val foreGround = theme.safeForegroundColor()
        switchNotifications.tint(accentColor, foreGround)
        checkBoxSound.tint(accentColor)
        checkBoxVibrate.tint(accentColor)
        textToolbarTitle.setTextColor(theme.safeBackgroundColor())
        toolbar.setBackgroundColor(accentColor)
        toolbar.navigationIcon?.setColorFilter(theme.safeBackgroundColor(), PorterDuff.Mode.MULTIPLY)
        if (Build.VERSION.SDK_INT >= 21) {
            val darkerAccentColor = Easel.darkerColor(accentColor)
            window.statusBarColor = darkerAccentColor
            window.navigationBarColor = darkerAccentColor
        }
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
        textLabelNotifications.setTextColor(foreGround)
        textTime.setTextColor(foreGround)
        textLabelTime.setTextColor(foreGround)
        textLabelSound.setTextColor(foreGround)
        textLabelVibrate.setTextColor(foreGround)
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

        textTime.text = timeFormat().format(timeToAlert.time)
        checkBoxSound.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationSound(this@NotificationActivity, isChecked) }
        checkBoxVibrate.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationVibrate(this@NotificationActivity, isChecked) }
    }

    private fun timeFormat(): SimpleDateFormat {
        return SimpleDateFormat("h:mm a", Locale.getDefault())
    }
}
