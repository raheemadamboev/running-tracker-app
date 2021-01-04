package xyz.teamgravity.runningtracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import xyz.teamgravity.runningtracker.model.RunModel

class RunViewModel @ViewModelInject constructor(
    private val repository: RunRepository
) : ViewModel() {

    private val allRunsSortedByDate = repository.getAllRunsSortedByDate()
    private val allRunsSortedByDuration = repository.getAllRunsSortedByDuration()
    private val allRunsSortedByCaloriesBurned = repository.getAllRunsSortedByCaloriesBurned()
    private val allRunsSortedByAverageSpeed = repository.getAllRunsSortedByAverageSpeed()
    private val allRunsSortedByDistance = repository.getAllRunsSortedByDistance()

    fun insert(run: RunModel) = repository.insert(run)

    fun delete(run: RunModel) = repository.delete(run)
}