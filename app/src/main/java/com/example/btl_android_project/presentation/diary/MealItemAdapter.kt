package com.example.btl_android_project.presentation.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.BasicItemMealBinding

class MealItemAdapter(
    private val items: List<MealItem>,
    private val onItemLongClick: (MealItem, View) -> Unit
) : RecyclerView.Adapter<MealItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = BasicItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(private val binding: BasicItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MealItem) {
            binding.nameTextView.text = item.name
            binding.servingTextView.text = item.serving
            binding.itemCaloriesTextView.text = item.calories.toString()

            binding.root.setOnLongClickListener {
                onItemLongClick(item, binding.root)
                true
            }
        }
    }
}
