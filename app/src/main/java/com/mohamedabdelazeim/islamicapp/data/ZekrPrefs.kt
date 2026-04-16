package com.mohamedabdelazeim.islamicapp.data

import android.content.Context

object ZekrPrefs {
    private const val PREFS = "islamic_app_prefs"

    // Keys
    private const val KEY_ENABLED = "is_enabled"
    private const val KEY_INTERVAL = "interval_minutes"
    private const val KEY_PLAYBACK_MODE = "playback_mode"
    private const val KEY_REPEAT_INDEX = "repeat_index"
    private const val KEY_CURRENT_INDEX = "current_index"

    // Playback Modes
    const val MODE_SEQUENTIAL = "sequential"
    const val MODE_REPEAT = "repeat"

    fun isEnabled(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, false)

    fun setEnabled(ctx: Context, v: Boolean) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putBoolean(KEY_ENABLED, v).apply()

    fun getIntervalMinutes(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_INTERVAL, 30)

    fun setIntervalMinutes(ctx: Context, v: Int) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_INTERVAL, v).apply()

    fun getPlaybackMode(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_PLAYBACK_MODE, MODE_SEQUENTIAL) ?: MODE_SEQUENTIAL

    fun setPlaybackMode(ctx: Context, v: String) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_PLAYBACK_MODE, v).apply()

    fun getRepeatIndex(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_REPEAT_INDEX, 0)

    fun setRepeatIndex(ctx: Context, v: Int) =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_REPEAT_INDEX, v).apply()

    fun nextZekrIndex(ctx: Context, listSize: Int): Int {
        val prefs = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val current = prefs.getInt(KEY_CURRENT_INDEX, 0)
        val next = (current + 1) % listSize
        prefs.edit().putInt(KEY_CURRENT_INDEX, next).apply()
        return current
    }

    fun getCurrentIndex(ctx: Context): Int =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_CURRENT_INDEX, 0)
}
