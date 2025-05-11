package com.example.btl_android_project.presentation.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealSectionBinding

class MealSectionAdapter(
    private var sections: List<MealSection>,
    private val onItemLongClick: (MealItem, View, Int) -> Unit,
) :
    RecyclerView.Adapter<MealSectionAdapter.SectionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemMealSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int = sections.size

    inner class SectionViewHolder(private val binding: ItemMealSectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(section: MealSection) {
            binding.mealTitleTextView.text = section.title
            binding.mealCaloriesTextView.text = section.calories.toString()

            binding.itemsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.itemsRecyclerView.adapter = MealItemAdapter(section.items, onItemLongClick)
        }
    }

    fun updateData(newSections: List<MealSection>) {
        sections = newSections
        notifyDataSetChanged()
    }
}
