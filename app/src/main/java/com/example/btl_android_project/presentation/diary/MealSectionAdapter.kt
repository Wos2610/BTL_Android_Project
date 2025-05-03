package com.example.btl_android_project.presentation.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R

class MealSectionAdapter(private var sections: List<MealSection>) :
    RecyclerView.Adapter<MealSectionAdapter.SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int = sections.size

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.mealTitleTextView)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.mealCaloriesTextView)
        private val itemsRecyclerView: RecyclerView = itemView.findViewById(R.id.itemsRecyclerView)

        fun bind(section: MealSection) {
            titleTextView.text = section.title
            caloriesTextView.text = section.calories.toString()

            // Set up nested RecyclerView for meal items
            itemsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            itemsRecyclerView.adapter = MealItemAdapter(section.items)
        }
    }

    fun updateData(newSections: List<MealSection>) {
        sections = newSections
        notifyDataSetChanged()
    }
}