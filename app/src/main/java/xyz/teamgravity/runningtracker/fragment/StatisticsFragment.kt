package xyz.teamgravity.runningtracker.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.databinding.FragmentStatisticsBinding
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.viewmodel.StatisticsViewModel
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val statisticsViewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        updateUI()
    }

    private fun updateUI() {
        binding.apply {
            statisticsViewModel.getTotalDuration().observe(viewLifecycleOwner) {
                it?.let { totalRun ->
                    totalTimeT.text = Helper.formatStopwatch(totalRun)
                }
            }

            statisticsViewModel.getTotalDistanceInMeters().observe(viewLifecycleOwner) {
                it?.let { totalDistance ->
                    totalDistanceT.text = "${round((totalDistance / 1000F) * 10F) / 10F} km"
                }
            }

            statisticsViewModel.getTotalAverageSpeed().observe(viewLifecycleOwner) {
                it?.let { averageSpeed ->
                    averageSpeedT.text = "${round(averageSpeed * 10F) / 10F} km/hour"
                }
            }

            statisticsViewModel.getTotalCaloriesBurned().observe(viewLifecycleOwner) {
                it?.let { totalCalories ->
                    totalCaloriesT.text = "$totalCalories kcal"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}