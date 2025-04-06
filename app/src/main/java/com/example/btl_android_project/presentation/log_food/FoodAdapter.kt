package com.example.btl_android_project.presentation.log_food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Food

class FoodAdapter(private val foods: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val meal = foods[position]
        val binding = ItemMealBinding.bind(holder.itemView)
        binding.tvMealName.text = meal.name
        binding.tvMealInfo.text = meal.calories.toString()
    }

    override fun getItemCount() = foods.size
}