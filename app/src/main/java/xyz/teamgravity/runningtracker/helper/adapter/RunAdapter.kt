package xyz.teamgravity.runningtracker.helper.adapter

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import xyz.teamgravity.runningtracker.R
import xyz.teamgravity.runningtracker.databinding.CardRunBinding
import xyz.teamgravity.runningtracker.helper.util.Helper
import xyz.teamgravity.runningtracker.model.RunModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RunAdapter @Inject constructor(
    private val dateFormat: SimpleDateFormat,
    private val res: Resources
) : ListAdapter<RunModel, RunAdapter.RunViewHolder>(DIFF_CALLBACK) {

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

    inner class RunViewHolder(private val binding: CardRunBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RunModel) {
            binding.apply {
                Glide.with(runI)
                    .load(model.image)
                    .into(runI)

                dateT.text = dateFormat.format(Date(model.timestamp))
                averageSpeedT.text = Helper.addTwoString((model.averageSpeedInKmh).toString(), res.getString(R.string.km_hour))
                distanceT.text = Helper.addTwoString((model.distanceInMeters / 1000F).toString(), res.getString(R.string.km))
                durationT.text = Helper.formatStopwatch(model.duration)
                caloriesT.text = Helper.addTwoString((model.caloriesBurned).toString(), res.getString(R.string.kcal))
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