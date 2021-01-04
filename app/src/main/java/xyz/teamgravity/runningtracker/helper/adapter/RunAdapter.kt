package xyz.teamgravity.runningtracker.helper.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import xyz.teamgravity.runningtracker.databinding.CardRunBinding
import xyz.teamgravity.runningtracker.model.RunModel

class RunAdapter: ListAdapter<RunModel, RunAdapter.RunViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RunModel>() {
            override fun areItemsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: RunModel, newItem: RunModel): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }
    }

    inner class RunViewHolder(private val binding: CardRunBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: RunModel) {
            binding.apply {

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