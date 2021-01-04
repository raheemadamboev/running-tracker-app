package xyz.teamgravity.runningtracker.helper.util

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions
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
}