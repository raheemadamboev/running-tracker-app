package xyz.teamgravity.runningtracker.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class RunModel(

    var image: Bitmap? = null,

    var averageSpeedInKmh: Float = 0F,
    var distanceInMeters: Int = 0,
    var caloriesBurned: Int = 0,

    var timestamp: Long = 0L,
    var duration: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var _id: Int? = null
}