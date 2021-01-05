package xyz.teamgravity.runningtracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class StatisticsViewModel @ViewModelInject constructor(
    private val repository: RunRepository
) : ViewModel() {

    fun getAllRunsSortedByDate() = repository.getAllRunsSortedByDate()

    fun getTotalDuration() = repository.getTotalDuration()

    fun getTotalCaloriesBurned() = repository.getTotalCaloriesBurned()

    fun getTotalDistanceInMeters() = repository.getTotalDistanceInMeters()

    fun getTotalAverageSpeed() = repository.getTotalAverageSpeed()
}