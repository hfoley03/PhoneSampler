package com.example.harryerayaudiorecorder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class NotificationHelper(private val context: Context) {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "com.example.harryerayaudiorecorder.audio"
        const val NOTIFICATION_CHANNEL_NAME = "Audio Recording"
        const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, importance)
            channel.description = "Channel for audio recording service"
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(): Notification {
        return Notification.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Recording in progress")
            .setContentText("Tap to return to app")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()
    }
}
