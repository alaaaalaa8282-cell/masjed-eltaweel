package com.mohamedabdelazeim.islamicapp.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import com.mohamedabdelazeim.islamicapp.MainActivity
import com.mohamedabdelazeim.islamicapp.R
import com.mohamedabdelazeim.islamicapp.data.ZekrData
import com.mohamedabdelazeim.islamicapp.data.ZekrPrefs

class ZekrService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val CHANNEL_ID = "zekr_channel"
        const val NOTIF_ID = 2001
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Smart pause: check calls and audio
        if (isCallActive() || isVoipCallActive()) {
            scheduleNext()
            stopSelf()
            return START_NOT_STICKY
        }

        val allAdhkar = ZekrData.loadAllAdhkar(this)
        if (allAdhkar.isEmpty()) {
            stopSelf()
            return START_NOT_STICKY
        }

        // Get the correct zekr based on playback mode
        val mode = ZekrPrefs.getPlaybackMode(this)
        val zekr = if (mode == ZekrPrefs.MODE_REPEAT) {
            val idx = ZekrPrefs.getRepeatIndex(this).coerceIn(0, allAdhkar.size - 1)
            allAdhkar[idx]
        } else {
            val idx = ZekrPrefs.nextZekrIndex(this, allAdhkar.size)
            allAdhkar[idx]
        }

        // Show foreground notification
        val notif = buildNotification(zekr.title, zekr.text)
        startForeground(NOTIF_ID, notif)

        // Play audio if available, otherwise show for 5 seconds
        if (zekr.audioRes != null) {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, zekr.audioRes)
            mediaPlayer?.setOnCompletionListener {
                it.release()
                scheduleNext()
                stopSelf()
            }
            mediaPlayer?.start()
        } else {
            android.os.Handler(mainLooper).postDelayed({
                scheduleNext()
                stopSelf()
            }, 6000)
        }

        return START_NOT_STICKY
    }

    // ── Smart Pause Logic ──────────────────────────────────────────────────────

    private fun isCallActive(): Boolean {
        return try {
            val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            tm.callState != TelephonyManager.CALL_STATE_IDLE
        } catch (e: Exception) { false }
    }

    private fun isVoipCallActive(): Boolean {
        return try {
            val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            am.mode == AudioManager.MODE_IN_CALL ||
            am.mode == AudioManager.MODE_IN_COMMUNICATION ||
            am.isMusicActive
        } catch (e: Exception) { false }
    }

    // ── Scheduling ─────────────────────────────────────────────────────────────

    private fun scheduleNext() {
        if (!ZekrPrefs.isEnabled(this)) return
        val intervalMs = ZekrPrefs.getIntervalMinutes(this).toLong() * 60 * 1000
        val triggerAt = System.currentTimeMillis() + intervalMs

        val intent = Intent(this, ZekrService::class.java)
        val pending = PendingIntent.getService(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pending)
    }

    // ── Wake Lock ──────────────────────────────────────────────────────────────

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "IslamicApp::ZekrWakeLock"
        ).apply { acquire(10 * 60 * 1000L) } // max 10 minutes
    }

    // ── Notification ───────────────────────────────────────────────────────────

    private fun buildNotification(title: String, text: String): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("🌙 $title")
            .setContentText(text.take(80))
            .setStyle(NotificationCompat.BigTextStyle().bigText(text.take(300)))
            .setContentIntent(pi)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
    }

    private fun createChannel() {
        val ch = NotificationChannel(
            CHANNEL_ID,
            "أذكار",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "إشعارات الأذكار اليومية"
            enableVibration(true)
            enableLights(true)
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(ch)
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        wakeLock?.let { if (it.isHeld) it.release() }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
