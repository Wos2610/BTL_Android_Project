package com.example.btl_android_project.presentation.log_weight

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R
import com.example.btl_android_project.local.entity.LogWeight

class LogWeightAdapter(
    private val onItemClick: (LogWeight) -> Unit
) : RecyclerView.Adapter<LogWeightAdapter.ViewHolder>() {

    private var items = listOf<LogWeight>()

    fun submitList(list: List<LogWeight>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvWeight = itemView.findViewById<TextView>(R.id.tvWeight)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)

        fun bind(item: LogWeight) {
            tvWeight.text = "${item.weight} kg"
            tvDate.text = item.date
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log_weight, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}
