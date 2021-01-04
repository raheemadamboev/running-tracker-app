package xyz.teamgravity.runningtracker.service

import android.annotation.SuppressLint
import android.app.PendingIntent
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.activity.MainActivity
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.injection.App

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

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
    private var timerIsEnabled = false
    private var lapTime = 0L
    private var totalTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // counter for service notification
    private val timeRunInSeconds = MutableLiveData<Long>()

    @SuppressLint("VisibleForTests")
    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this) {
            updateLocationTracking(it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME -> {
                    if (isFirstRun) {
                        startForegroundService()
                        println("debug: Started service")
                        isFirstRun = false
                    } else {
                        startTimer()
                        println("debug: Resumed service")
                    }
                }

                ACTION_PAUSE -> {
                    pauseService()
                    println("debug: Paused service")
                }

                ACTION_STOP ->
                    println("debug: Stopped service")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

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
                        println("debug: ${location.altitude}, ${location.longitude}")
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

        val notificationBuilder = NotificationCompat.Builder(this, App.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle(resources.getString(R.string.app_name))
            .setContentText(resources.getString(R.string.total_time_start))
            .setContentIntent(mainActivityPendingIntent())

        startForeground(App.NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun pauseService() {
        isTracking.postValue(false)
        timerIsEnabled = false
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