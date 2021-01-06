package xyz.teamgravity.runningtracker.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xyz.teamgravity.runningtracker.model.RunModel
import javax.inject.Inject

class RunRepository @Inject constructor(
    private val dao: RunDao
) {

    fun insert(run: RunModel) {
        CoroutineScope(Dispatchers.IO).launch {
            dao.insert(run)
        }
    }

    fun getAllRunsSortedByDate() = dao.getAllRunsSortedByDate()

    fun getAllRunsSortedByDuration() = dao.getAllRunsSortedByDuration()

    fun getAllRunsSortedByCaloriesBurned() = dao.getAllRunsSortedByCaloriesBurned()

    fun getAllRunsSortedByAverageSpeed() = dao.getAllRunsSortedByAverageSpeed()

    fun getAllRunsSortedByDistance() = dao.getAllRunsSortedByDistance()

    // statistics
    fun getTotalDuration() = dao.getTotalDuration()

    fun getTotalCaloriesBurned() = dao.getTotalCaloriesBurned()

    fun getTotalDistanceInMeters() = dao.getTotalDistanceInMeters()

    fun getTotalAverageSpeed() = dao.getTotalAverageSpeed()
}