package com.example.btl_android_project.presentation.log_meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Meal

class MealAdapter(private val meals: List<Meal>) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        val binding = ItemMealBinding.bind(holder.itemView)
        binding.tvMealName.text = meal.name
        binding.tvMealInfo.text = meal.totalCalories.toString()
    }

    override fun getItemCount() = meals.size
}
