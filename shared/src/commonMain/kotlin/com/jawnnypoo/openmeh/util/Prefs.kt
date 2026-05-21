package com.jawnnypoo.openmeh.util

import com.russhwolf.settings.Settings

/**
 * App-wide notification preferences. Initialized once by the platform entry point with a
 * platform-specific [Settings] (Android: `SharedPreferencesSettings`; iOS would use
 * `NSUserDefaultsSettings`).
 */
object Prefs {

    private const val KEY_VIBRATE = "notification_vibrate"
    private const val KEY_SOUND = "notification_sound"
    private const val KEY_ENABLED = "notification_enabled"
    private const val KEY_HOUR = "notification_hour"
    private const val KEY_MINUTE = "notification_minute"

    private lateinit var settings: Settings

    fun initialize(settings: Settings) {
        this.settings = settings
    }

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
}
