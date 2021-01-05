package xyz.teamgravity.runningtracker.helper.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
import xyz.teamgravity.runningtracker.service.Polyline
import java.util.*

object Helper {

    /**
     * checks if location permissions granted according to api level
     */
    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }


    /**
     * return formatted time for stop watch
     */
    fun formatStopwatch(ms: Long, includeMillis: Boolean = false): String {
        val hour = (ms / 1000) / 60 / 60
        val minute = (ms / 1000) / 60 % 60
        val second = (ms / 1000) % 60

        return if (includeMillis) {
            val milliseconds = ms % 1000
            String.format(Locale.getDefault(), "%02d:%02d:%02d:%03d", hour, minute, second, milliseconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second)
        }
    }

    /**
     * adds all distance in polylines (list of LatLng) in meters
     */
    fun calculatePolylineLength(polyline: Polyline): Float {
        var distance = 0F

        for (i in 0..polyline.size - 2) {
            val pos1 = polyline[i]
            val pos2 = polyline[i + 1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }

        return distance
    }

    /**
     * add two string with empty space
     */
    fun addTwoString(one: String, two: String) = "$one $two"
}