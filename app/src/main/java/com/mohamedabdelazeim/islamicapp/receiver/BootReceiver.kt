package com.mohamedabdelazeim.islamicapp.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mohamedabdelazeim.islamicapp.data.ZekrPrefs
import com.mohamedabdelazeim.islamicapp.service.ZekrService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            if (!ZekrPrefs.isEnabled(ctx)) return

            // Schedule first alarm after boot
            val intervalMs = ZekrPrefs.getIntervalMinutes(ctx).toLong() * 60 * 1000
            val triggerAt = System.currentTimeMillis() + intervalMs

            val serviceIntent = Intent(ctx, ZekrService::class.java)
            val pending = PendingIntent.getService(
                ctx, 0, serviceIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
        }
    }
}
