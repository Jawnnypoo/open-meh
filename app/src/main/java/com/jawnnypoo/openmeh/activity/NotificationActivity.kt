package com.jawnnypoo.openmeh.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import com.commit451.easel.tint
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.util.Prefs
import com.jawnnypoo.openmeh.worker.ReminderWorker
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.coroutines.launch
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
        launch {
            ReminderWorker.schedule(this@NotificationActivity, hourOfDay, minute)
        }
        // Recreate for next time, starting with the newly set time
        timePickerDialog?.setInitialSelection(hourOfDay, minute)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        textToolbarTitle.setText(R.string.action_notifications)

        timeToAlert.set(Calendar.HOUR_OF_DAY, Prefs.notificationHour)
        timeToAlert.set(Calendar.MINUTE, Prefs.notificationMinute)
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
        // Tint widgets
        val accentColor = theme.safeAccentColor()
        val foreground = theme.safeForegroundColor()
        switchNotifications.tint(accentColor, foreground)
        checkBoxSound.tint(accentColor)
        checkBoxVibrate.tint(accentColor)
        textToolbarTitle.setTextColor(theme.safeBackgroundColor())
        toolbar.setBackgroundColor(accentColor)
        toolbar.navigationIcon?.setColorFilter(theme.safeBackgroundColor(), PorterDuff.Mode.MULTIPLY)
        window.statusBarColor = accentColor
        window.navigationBarColor = accentColor
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
        textLabelNotifications.setTextColor(foreground)
        textTime.setTextColor(foreground)
        textLabelTime.setTextColor(foreground)
        textLabelSound.setTextColor(foreground)
        textLabelVibrate.setTextColor(foreground)
        timePickerDialog?.accentColor = accentColor
        timePickerDialog?.setOkColor(Color.WHITE)
        timePickerDialog?.setCancelColor(Color.WHITE)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.do_nothing, R.anim.fade_out)
    }

    private fun setupUi() {
        switchNotifications.isChecked = Prefs.areNotificationsEnabled
        checkBoxSound.isChecked = Prefs.getNotificationSound()
        checkBoxVibrate.isChecked = Prefs.getNotificationVibrate()
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Prefs.areNotificationsEnabled = isChecked
            if (isChecked) {
                launch {
                    ReminderWorker.cancel(this@NotificationActivity)
                }
            } else {
                launch {
                    ReminderWorker.schedule(this@NotificationActivity, Prefs.notificationHour, Prefs.notificationMinute)
                }
            }
        }

        textTime.text = timeFormat().format(timeToAlert.time)
        checkBoxSound.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationSound(isChecked) }
        checkBoxVibrate.setOnCheckedChangeListener { _, isChecked -> Prefs.setNotificationVibrate(isChecked) }
    }

    private fun timeFormat(): SimpleDateFormat {
        return SimpleDateFormat("h:mm a", Locale.getDefault())
    }
}
