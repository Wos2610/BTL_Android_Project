package com.example.btl_android_project.presentation.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R

class MealItemAdapter(private val items: List<MealItem>) :
    RecyclerView.Adapter<MealItemAdapter.ItemViewHolder>() {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.basic_item_meal, parent, false)
        return ItemViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }
    
    override fun getItemCount(): Int = items.size
    
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val servingTextView: TextView = itemView.findViewById(R.id.servingTextView)
        private val caloriesTextView: TextView = itemView.findViewById(R.id.itemCaloriesTextView)
        
        fun bind(item: MealItem) {
            nameTextView.text = item.name
            servingTextView.text = item.serving
            caloriesTextView.text = item.calories.toString()
        }
    }
}