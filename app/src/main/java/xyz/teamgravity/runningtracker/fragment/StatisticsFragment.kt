package xyz.teamgravity.runningtracker.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.FragmentStatisticsBinding
import xyz.teamgravity.runningtracker.helper.DialogRun
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

        activity?.let {
            updateUI(it)
        }
    }

    private fun updateUI(activity: FragmentActivity) {
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

            barChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawLabels(false)
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }

            barChart.axisLeft.apply {
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }

            barChart.axisRight.apply {
                axisLineColor = Color.WHITE
                textColor = Color.WHITE
                setDrawGridLines(false)
            }

            barChart.apply {
                description.text = "Avg speed over time"
                legend.isEnabled = false
            }

            statisticsViewModel.getAllRunsSortedByDate().observe(viewLifecycleOwner) {
                val allAverageSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i].averageSpeedInKmh) }
                val barDataSet = BarDataSet(allAverageSpeed, "Avg speed over time").apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(activity, R.color.colorAccent)
                }
                barChart.data = BarData(barDataSet)
                barChart.marker = DialogRun(it.reversed(), activity, R.layout.dialog_run)
                barChart.invalidate()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}