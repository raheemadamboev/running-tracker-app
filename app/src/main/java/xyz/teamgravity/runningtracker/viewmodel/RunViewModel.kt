package xyz.teamgravity.runningtracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import xyz.teamgravity.runningtracker.model.RunModel

class RunViewModel @ViewModelInject constructor(
    private val repository: RunRepository
) : ViewModel() {

    fun insert(run: RunModel) = repository.insert(run)

    fun delete(run: RunModel) = repository.delete(run)

    fun getAllRunsSortedByDate() = repository.getAllRunsSortedByDate()
}