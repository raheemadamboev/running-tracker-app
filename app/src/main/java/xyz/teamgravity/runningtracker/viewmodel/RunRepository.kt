package xyz.teamgravity.runningtracker.viewmodel

import xyz.teamgravity.runningtracker.model.RunModel
import javax.inject.Inject

class RunRepository @Inject constructor(
    private val dao: RunDao
) {

    suspend fun insert(run: RunModel) = dao.insert(run)

    suspend fun delete(run: RunModel) = dao.delete(run)

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