package xyz.teamgravity.runningtracker.viewmodel

import androidx.lifecycle.LiveData
import androidx.room.*
import xyz.teamgravity.runningtracker.model.RunModel

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(run: RunModel)

    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<RunModel>>

    @Query("SELECT * FROM run_table ORDER BY duration DESC")
    fun getAllRunsSortedByDuration(): LiveData<List<RunModel>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<RunModel>>

    @Query("SELECT * FROM run_table ORDER BY averageSpeedInKmh DESC")
    fun getAllRunsSortedByAverageSpeed(): LiveData<List<RunModel>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<RunModel>>

    // statistics
    @Query("SELECT SUM(duration) FROM run_table")
    fun getTotalDuration(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCaloriesBurned(): LiveData<Long>

    @Query("SELECT SUM(distanceInMeters) FROM run_table")
    fun getTotalDistanceInMeters(): LiveData<Long>

    @Query("SELECT AVG(averageSpeedInKmh) FROM run_table")
    fun getTotalAverageSpeed(): LiveData<Float>
}