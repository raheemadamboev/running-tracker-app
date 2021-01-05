package xyz.teamgravity.runningtracker.helper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import xyz.teamgravity.runningtracker.databinding.CardRunBinding
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.model.RunModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RunAdapter : ListAdapter<RunModel, RunAdapter.RunViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RunModel>() {
            override fun areItemsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
                return oldItem.averageSpeedInKmh == newItem.averageSpeedInKmh
                        && oldItem.caloriesBurned == newItem.caloriesBurned
                        && oldItem.distanceInMeters == newItem.distanceInMeters
                        && oldItem.duration == newItem.duration
                        && oldItem.timestamp == newItem.timestamp
            }
        }
    }

    @Inject
    lateinit var dateFormat: SimpleDateFormat

    inner class RunViewHolder(private val binding: CardRunBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RunModel) {
            binding.apply {
                Glide.with(runI)
                    .load(model.image)
                    .into(runI)

                dateT.text = dateFormat.format(Date(model.timestamp))
                // TODO proper
                averageSpeedT.text = "${model.averageSpeedInKmh} km/h"
                distanceT.text = "${model.distanceInMeters / 1000F} km"
                durationT.text = Helper.formatStopwatch(model.duration)
                caloriesT.text = "${model.caloriesBurned} kcal"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(CardRunBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}