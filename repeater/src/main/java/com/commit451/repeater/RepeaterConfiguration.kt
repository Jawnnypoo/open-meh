package com.commit451.repeater

/**
 * Configuration for a repeating function
 */
data class RepeaterConfiguration(
        /**
         * The tag, or unique identifier for this repeating function
         */
        val tag: String,
        /**
         * The hour of day to execute. Defaults to [Int.MIN_VALUE] if fetched and not set
         */
        val hour: Int,
        /**
         * The minute of day to execute. Defaults to [Int.MAX_VALUE] if fetched and not set
         */
        val minute: Int
) {

    fun isScheduled(): Boolean {
        return hour != Int.MIN_VALUE && minute != Int.MIN_VALUE
    }
}
