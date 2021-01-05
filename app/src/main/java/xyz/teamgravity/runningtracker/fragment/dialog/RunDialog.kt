package xyz.teamgravity.runningtracker.fragment.dialog

import android.annotation.SuppressLint
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.textview.MaterialTextView
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.model.RunModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ViewConstructor")
class RunDialog(
    private val runs: List<RunModel>,
    context: Context,
    layoutId: Int
) : MarkerView(context, layoutId) {

    private val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e != null) {
            val runId = e.x.toInt()

            rootView.apply {
                findViewById<MaterialTextView>(R.id.date_t).text = dateFormat.format(Date(runs[runId].timestamp))
                findViewById<MaterialTextView>(R.id.average_speed_t).text = "${runs[runId].averageSpeedInKmh} km/h"
                findViewById<MaterialTextView>(R.id.distance_t).text = "${runs[runId].distanceInMeters / 1000F} km"
                findViewById<MaterialTextView>(R.id.duration_t).text = Helper.formatStopwatch(runs[runId].duration)
                findViewById<MaterialTextView>(R.id.calories_t).text = "${runs[runId].caloriesBurned} kcal"
            }
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2F, -height.toFloat())
    }
}