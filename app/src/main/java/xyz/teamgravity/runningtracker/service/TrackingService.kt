package xyz.teamgravity.runningtracker.service

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import timber.log.Timber
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.activity.MainActivity
import xyz.teamgravity.runningtracker.injection.App

class TrackingService : LifecycleService() {
    companion object {
        const val ACTION_START_OR_RESUME = "actionStartOrResume"
        const val ACTION_PAUSE = "actionPause"
        const val ACTION_STOP = "actionStop"
        const val ACTION_SHOW_TRACKING_FRAGMENT = "actionShowTrackingFragment"
    }

    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME -> {
                    if (isFirstRun) {
                        startForegroundService()
                        Timber.d("Started service")
                        isFirstRun = false
                    } else {
                        Timber.d("Resumed service")
                    }

                    Timber.d("Started or resumed service")
                }

                ACTION_PAUSE ->
                    Timber.d("Paused service")

                ACTION_STOP ->
                    Timber.d("Stopped service")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        val notificationBuilder = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.total_time_start))
            .setContentIntent(mainActivityPendingIntent())

        startForeground(App.NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun mainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).also { intent ->
                intent.action = ACTION_SHOW_TRACKING_FRAGMENT
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )
}