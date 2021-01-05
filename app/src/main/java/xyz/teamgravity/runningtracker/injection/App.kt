package xyz.teamgravity.runningtracker.injection

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.HiltAndroidApp
import xyz.teamgravity.runningtracker.helper.constants.Notifications

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    // notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Notifications.NOTIFICATION_CHANNEL_ID, Notifications.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}