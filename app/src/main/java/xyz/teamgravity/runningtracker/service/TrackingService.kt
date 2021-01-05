package xyz.teamgravity.runningtracker.service

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.helper.constants.Notifications
import xyz.teamgravity.runningtracker.helper.util.Helper
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    companion object {
        const val ACTION_START_OR_RESUME = "actionStartOrResume"
        const val ACTION_PAUSE = "actionPause"
        const val ACTION_STOP = "actionStop"
        const val ACTION_SHOW_TRACKING_FRAGMENT = "actionShowTrackingFragment"

        const val LOCATION_UPDATE_INTERVAL = 5000L
        const val FASTEST_LOCATION_INTERVAL = 2000L

        const val TIMER_UPDATE_INTERVAL = 50L

        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val timeRunInMillis = MutableLiveData<Long>()
    }

    private var isFirstRun = true
    private var serviceKilled = false
    private var timerIsEnabled = false
    private var lapTime = 0L
    private var totalTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    private lateinit var currentNotificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    // counter for service notification
    private val timeRunInSeconds = MutableLiveData<Long>()

    override fun onCreate() {
        super.onCreate()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBuilder = baseNotificationBuilder

        postInitialValues()

        isTracking.observe(this) {
            if (!serviceKilled) {
                updateLocationTracking(it)
                updateNotification(it)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME -> {
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        startTimer()
                    }
                }

                ACTION_PAUSE -> {
                    pauseService()
                }

                ACTION_STOP -> {
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // start timer
    private fun startTimer() {
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        timerIsEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                // time difference between now and timeStarted
                lapTime = System.currentTimeMillis() - timeStarted
                // post the new lap time
                timeRunInMillis.postValue(totalTime + lapTime)

                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }

                delay(TIMER_UPDATE_INTERVAL)
            }

            totalTime += lapTime
        }
    }

    // update notification
    private fun updateNotification(isTracking: Boolean) {
        val pendingIntent = if (isTracking) {
            val pauseIntent = Intent(this, TrackingService::class.java)
            pauseIntent.action = ACTION_PAUSE

            PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        } else {
            val resumeIntent = Intent(this, TrackingService::class.java)
            resumeIntent.action = ACTION_START_OR_RESUME

            PendingIntent.getService(this, 2, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        // first clear previous actions otherwise we will have lotta actions
        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        currentNotificationBuilder = baseNotificationBuilder
            .addAction(
                if (isTracking) R.drawable.ic_pause else R.drawable.ic_play,
                if (isTracking) resources.getString(R.string.pause) else resources.getString(R.string.resume),
                pendingIntent
            )

        notificationManager.notify(Notifications.RUN_NOTIFICATION_ID, currentNotificationBuilder.build())
    }

    // stop service
    private fun killService() {
        serviceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        stopForeground(true)
        stopSelf()
    }

    // default values
    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
    }

    // when we stop tracking we need to add empty polyline
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    // last path point
    private fun addPathPoint(location: Location?) {
        location?.let {
            val position = LatLng(it.latitude, it.longitude)
            pathPoints.value?.apply {
                last().add(position)
                pathPoints.postValue(this)
            }
        }
    }

    // location change callback to add location to the last polyline
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)

            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (Helper.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }

                fusedLocationProviderClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // start foreground service
    private fun startForegroundService() {
        startTimer()

        isTracking.postValue(true)

        startForeground(Notifications.RUN_NOTIFICATION_ID, baseNotificationBuilder.build())

        // update notification
        timeRunInSeconds.observe(this) {
            if (!serviceKilled) {
                val notification = currentNotificationBuilder
                    .setContentText(Helper.formatStopwatch(it * 1000L))

                notificationManager.notify(Notifications.RUN_NOTIFICATION_ID, notification.build())
            }
        }
    }

    // pause service
    private fun pauseService() {
        isTracking.postValue(false)
        timerIsEnabled = false
    }
}