package com.jawnnypoo.openmeh.activity

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.commit451.addendum.design.snackbar
import com.commit451.easel.tint
import com.google.android.material.snackbar.Snackbar
import com.jawnnypoo.openmeh.R
import com.jawnnypoo.openmeh.databinding.ActivityNotificationsBinding
import com.jawnnypoo.openmeh.model.ParsedTheme
import com.jawnnypoo.openmeh.util.Prefs
import com.jawnnypoo.openmeh.worker.ReminderWorker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Notify all the things!
 */
class NotificationActivity : BaseActivity() {

    companion object {

        fun newInstance(context: Context, theme: ParsedTheme?): Intent {
            val intent = Intent(context, NotificationActivity::class.java)
            intent.putExtra(KEY_THEME, theme)
            return intent
        }
    }

    private lateinit var binding: ActivityNotificationsBinding
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var timeToAlert: Calendar = Calendar.getInstance()

    private val onTimeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        timeToAlert.set(Calendar.HOUR_OF_DAY, hourOfDay)
        timeToAlert.set(Calendar.MINUTE, minute)
        Prefs.notificationHour = hourOfDay
        Prefs.notificationMinute = minute
        binding.textTime.text = timeFormat().format(timeToAlert.time)
        if (binding.switchNotifications.isChecked) {
            schedule()
        } else {
            binding.switchNotifications.isChecked = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp)
        binding.toolbar.setNavigationOnClickListener { goBack() }
        binding.textToolbarTitle.setText(R.string.action_notifications)

        timeToAlert.set(Calendar.HOUR_OF_DAY, Prefs.notificationHour)
        timeToAlert.set(Calendar.MINUTE, Prefs.notificationMinute)
        setupUi()
        binding.rootNotifications.setOnClickListener { binding.switchNotifications.toggle() }
        binding.rootNotificationTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this,
                onTimeSetListener,
                timeToAlert.get(Calendar.HOUR_OF_DAY),
                timeToAlert.get(Calendar.MINUTE),
                false
            )
            timePickerDialog.updateTime(Prefs.notificationHour, Prefs.notificationMinute)
            timePickerDialog.show()
        }
        binding.rootNotificationSound.setOnClickListener { binding.checkBoxSound.toggle() }
        binding.rootVibrate.setOnClickListener { binding.checkBoxVibrate.toggle() }
        val theme = intent.getParcelableExtra<ParsedTheme>(KEY_THEME)
        if (theme != null) {
            applyTheme(theme)
        }
        notificationPermissionStuff()
    }

    private fun notificationPermissionStuff() {
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Snackbar.make(
                        binding.root,
                        "Notification permission is not enabled",
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun applyTheme(theme: ParsedTheme) {
        // Tint widgets
        val accentColor = theme.safeAccentColor()
        val foreground = theme.safeForegroundColor()
        binding.switchNotifications.tint(accentColor, foreground)
        binding.checkBoxSound.tint(accentColor)
        binding.checkBoxVibrate.tint(accentColor)
        binding.textToolbarTitle.setTextColor(theme.safeBackgroundColor())
        binding.toolbar.setBackgroundColor(accentColor)
        binding.toolbar.navigationIcon?.setColorFilter(
            theme.safeBackgroundColor(),
            PorterDuff.Mode.MULTIPLY
        )
        window.statusBarColor = accentColor
        window.navigationBarColor = accentColor
        window.decorView.setBackgroundColor(theme.safeBackgroundColor())
        binding.textLabelNotifications.setTextColor(foreground)
        binding.textTime.setTextColor(foreground)
        binding.textLabelTime.setTextColor(foreground)
        binding.textLabelSound.setTextColor(foreground)
        binding.textLabelVibrate.setTextColor(foreground)
    }

    private fun setupUi() {
        binding.switchNotifications.isChecked = Prefs.areNotificationsEnabled
        binding.checkBoxSound.isChecked = Prefs.isNotificationSound
        binding.checkBoxVibrate.isChecked = Prefs.isNotificationVibrate
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            Prefs.areNotificationsEnabled = isChecked
            if (isChecked) {
                schedule()
            } else {
                cancel()
            }
        }

        binding.textTime.text = timeFormat().format(timeToAlert.time)
        binding.checkBoxSound.setOnCheckedChangeListener { _, isChecked ->
            Prefs.isNotificationSound = isChecked
        }
        binding.checkBoxVibrate.setOnCheckedChangeListener { _, isChecked ->
            Prefs.isNotificationVibrate = isChecked
        }
    }

    private fun timeFormat(): SimpleDateFormat {
        return SimpleDateFormat("h:mm a", Locale.getDefault())
    }

    private fun schedule() {
        launch {
            ReminderWorker.schedule(
                this@NotificationActivity,
                Prefs.notificationHour,
                Prefs.notificationMinute
            )
            binding.root.snackbar(R.string.notification_scheduled)
        }
    }

    private fun cancel() {
        launch {
            ReminderWorker.cancel(this@NotificationActivity)
            binding.root.snackbar(R.string.notification_canceled)
        }
    }
}
