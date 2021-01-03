package xyz.teamgravity.myapplication.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel

class StatisticsViewModel @ViewModelInject constructor(
    val repository: RunRepository
): ViewModel() {
}