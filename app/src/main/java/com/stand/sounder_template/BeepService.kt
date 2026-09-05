package com.stand.sounder_template

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Collections

class BeepService : Service() {
    private val binder = LocalBinder()
    private val notificationId: Int = 1 // 修改后的通知 ID
    /* 只在服务内存活期间持有播放器，服务死就释放 */
    private val livePlayers = Collections.synchronizedSet(mutableSetOf<MediaPlayer>())
    inner class LocalBinder : Binder() {
        fun getService(): BeepService = this@BeepService
    }

    override fun onBind(intent: Intent): IBinder = binder

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        playParallelBeep()

        startForeground(notificationId, createNotification())

        return START_NOT_STICKY
    }

    fun playParallelBeep() {
        val afd = BeepManager.randomAssetFd(this)
        val mp = MediaPlayer()
        livePlayers += mp
        mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        mp.setOnPreparedListener {
            afd.close()
            it.start()
        }
        mp.setOnCompletionListener {
            it.release(); livePlayers -= it
        }
        mp.setOnErrorListener { _, _, _ ->
            mp.release(); livePlayers -= mp; true
        }
        mp.prepareAsync()
    }

    private fun createNotification(): Notification {
        val channelId = getString(R.string.channel_id)
        val channelName = getString(R.string.app_name)
        val text = getString(R.string.notice_title)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_MIN
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(text)
            .setSmallIcon(R.mipmap.ic_launcher) // 必须设置一个有效图标
            .build()
    }
}