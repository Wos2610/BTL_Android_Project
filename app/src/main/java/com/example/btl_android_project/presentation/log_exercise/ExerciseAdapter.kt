package com.example.btl_android_project.presentation.log_exercise

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R
import com.example.btl_android_project.local.entity.Exercise

class ExerciseAdapter(
    private val onExerciseClick: (Exercise) -> Unit,
    private val onLogExerciseClick: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder>() {

    private val exercises = mutableListOf<Exercise>()

    fun setExercises(list: List<Exercise>) {
        exercises.clear()
        exercises.addAll(list)
        notifyDataSetChanged()
    }

    fun getExerciseAt(position: Int): Exercise = exercises[position]

    fun removeAt(position: Int) {
        exercises.removeAt(position)
        notifyItemRemoved(position)
    }

    inner class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvExerciseName)
        private val tvTime: TextView = itemView.findViewById(R.id.tvExerciseTime)
        private val btnAdd: ImageView = itemView.findViewById(R.id.btnAdd)

        fun bind(item: Exercise) {
            tvName.text = item.description
            tvTime.text = "${item.minutesPerformed} minutes - " + "${item.caloriesBurned} calories"
            itemView.setOnClickListener { onExerciseClick(item) }
            btnAdd.setOnClickListener {
                onLogExerciseClick(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_exercise, parent, false)
        return ExerciseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bind(exercises[position])
    }

    override fun getItemCount(): Int = exercises.size
}

