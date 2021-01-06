package xyz.teamgravity.runningtracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
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

    val runs = MediatorLiveData<List<RunModel>>()
    var sortType = RunSortType.DATE

    init {
        runs.addSource(allRunsSortedByDate) { result ->
            if (sortType == RunSortType.DATE) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(allRunsSortedByDuration) { result ->
            if (sortType == RunSortType.DURATION) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(allRunsSortedByCaloriesBurned) { result ->
            if (sortType == RunSortType.CALORIES_BURNED) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(allRunsSortedByAverageSpeed) { result ->
            if (sortType == RunSortType.AVERAGE_SPEED) {
                result?.let { runs.value = it }
            }
        }

        runs.addSource(allRunsSortedByDistance) { result ->
            if (sortType == RunSortType.DISTANCE) {
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRuns(sortType: RunSortType) = when(sortType) {
        RunSortType.DATE -> allRunsSortedByDate.value?.let { runs.value = it }
        RunSortType.DURATION -> allRunsSortedByDuration.value?.let { runs.value = it }
        RunSortType.CALORIES_BURNED -> allRunsSortedByCaloriesBurned.value?.let { runs.value = it }
        RunSortType.AVERAGE_SPEED -> allRunsSortedByAverageSpeed.value?.let { runs.value = it }
        RunSortType.DISTANCE -> allRunsSortedByDistance.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insert(run: RunModel) = repository.insert(run)
}