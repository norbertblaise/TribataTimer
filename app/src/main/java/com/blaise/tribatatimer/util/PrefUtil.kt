package com.blaise.tribatatimer.util

import android.content.Context
import android.preference.PreferenceManager
import com.blaise.tribatatimer.session.CurrentSessionFragment
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.Preferences

class PrefUtil {
    companion object {
        public fun getTimerLength(context: Context): Int {
            //placeholder
            return 1
        }

        private val PREVIOUS_TIMER_LENGTH_SECONDS_ID =
            "com.blaise.tribatatimer.previous_timer_length"

        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        public fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        private val TIMER_STATE_ID = "com.blaise.tribatatimer.timer_state"

        public fun getTimerState(context: Context): CurrentSessionFragment.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return CurrentSessionFragment.TimerState.values()[ordinal]
        }

        public fun setTimerState(state: CurrentSessionFragment.TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }

        private val SECONDS_REMAINING_ID = "com.blaise.tribatatimer.timer_seconds_remaining"
        public fun getSecondsRemaining(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)

            return preferences.getLong(SECONDS_REMAINING_ID, 0)
        }

        public fun setSecondsRemaining(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID, seconds)
            editor.apply()
        }
    }
}