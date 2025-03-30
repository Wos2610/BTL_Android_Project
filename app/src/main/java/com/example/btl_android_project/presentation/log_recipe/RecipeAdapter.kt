package com.example.btl_android_project.presentation.log_recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Recipe

class RecipeAdapter(private val recipes: List<Recipe>) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val meal = recipes[position]
        val binding = ItemMealBinding.bind(holder.itemView)
        binding.tvMealName.text = meal.name
        binding.tvMealInfo.text = meal.calories.toString()
    }

    override fun getItemCount() = recipes.size
}
