package com.commit451.repeater

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Stores the prefs
 */
internal object RepeaterPrefs {

    private const val PREFS_FILE_NAME = "com.commit451.repeater.PREFS"

    private const val KEY_CONFIG_TAGS = "config_tags"
    private const val KEY_SUFFIX_HOUR = "_hour"
    private const val KEY_SUFFIX_MINUTE = "_minute"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun store(configuration: RepeaterConfiguration) {
        modifySet {
            it.add(configuration.tag)
        }
    }

    fun remove(tag: String) {
        // Should we also remove all the associated prefs? hour, minute?
        modifySet {
            it.remove(tag)
        }
    }

    internal fun configs(): List<RepeaterConfiguration> {
        return prefs().getStringSetNotNull(KEY_CONFIG_TAGS)
                .map {
                    config(it)
                }
    }

    fun config(tag: String): RepeaterConfiguration {
        return RepeaterConfiguration(
                tag,
                hour(tag),
                minute(tag)
        )
    }

    private fun hour(tag: String): Int {
        return prefs().getInt("$tag-$KEY_SUFFIX_HOUR", 0)
    }

    private fun minute(tag: String): Int {
        return prefs().getInt("$tag-$KEY_SUFFIX_MINUTE", 0)
    }

    private fun modifySet(block: (set: MutableSet<String>) -> Unit) {
        val tags = prefs().getStringSetNotNull(KEY_CONFIG_TAGS)
                .toMutableSet()
        block.invoke(tags)
        prefs().edit {
            putStringSet(KEY_CONFIG_TAGS, tags)
        }
    }

    private fun prefs(): SharedPreferences {
        return prefs
                ?: throw IllegalStateException("Repeater not initialized. You need to call Repeater.init() in your Application class")
    }

    private fun SharedPreferences.getStringSetNotNull(key: String): Set<String> {
        return getStringSet(key, setOf()) ?: setOf()
    }
}
