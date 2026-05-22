package com.jawnnypoo.openmeh.util

import com.russhwolf.settings.Settings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

/**
 * App-wide notification preferences, backed by a platform-specific [Settings] passed in via the DI
 * graph factory (Android: `SharedPreferencesSettings`; iOS would use `NSUserDefaultsSettings`).
 */
@Inject
@SingleIn(AppScope::class)
class Prefs(private val settings: Settings) {

    var isNotificationSound: Boolean
        get() = settings.getBoolean(KEY_SOUND, false)
        set(value) {
            settings.putBoolean(KEY_SOUND, value)
        }

    var isNotificationVibrate: Boolean
        get() = settings.getBoolean(KEY_VIBRATE, false)
        set(value) {
            settings.putBoolean(KEY_VIBRATE, value)
        }

    var notificationHour: Int
        get() = settings.getInt(KEY_HOUR, 18)
        set(value) {
            settings.putInt(KEY_HOUR, value)
        }

    var notificationMinute: Int
        get() = settings.getInt(KEY_MINUTE, 30)
        set(value) {
            settings.putInt(KEY_MINUTE, value)
        }

    var areNotificationsEnabled: Boolean
        get() = settings.getBoolean(KEY_ENABLED, false)
        set(value) {
            settings.putBoolean(KEY_ENABLED, value)
        }

    companion object {
        private const val KEY_VIBRATE = "notification_vibrate"
        private const val KEY_SOUND = "notification_sound"
        private const val KEY_ENABLED = "notification_enabled"
        private const val KEY_HOUR = "notification_hour"
        private const val KEY_MINUTE = "notification_minute"
    }
}
