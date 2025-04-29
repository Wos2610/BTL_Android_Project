package com.example.btl_android_project.presentation.log_recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Recipe

class RecipeAdapter(
    private var recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit,
    private val onAddToDiaryClick: (Recipe) -> Unit,
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        val binding = ItemMealBinding.bind(holder.itemView)
        binding.tvMealName.text = recipe.name
        binding.tvMealInfo.text = recipe.calories.toString()
        binding.btnAddMeal.setOnClickListener {
            onAddToDiaryClick(recipe)
        }
        binding.root.setOnClickListener {
            onItemClick(recipe)
        }
    }

    override fun getItemCount() = recipes.size

    fun updateData(newRecipes: List<Recipe>) {
        if(recipes.isNotEmpty()) {
            (recipes as MutableList).clear()
            (recipes as MutableList<Recipe>).addAll(newRecipes)
        }
        else{
            recipes = newRecipes
        }
        notifyDataSetChanged()
    }
}
