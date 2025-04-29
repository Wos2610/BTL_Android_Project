package com.example.btl_android_project.presentation.log_meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R
import com.example.btl_android_project.databinding.ItemHeaderBinding
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.databinding.ItemSearchIngredientBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.Meal
import com.example.btl_android_project.local.entity.Recipe

class MealAdapter(private var items: MutableList<MealItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_RECIPE = 1
        private const val TYPE_FOOD = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MealItem.RecipeItem -> TYPE_RECIPE
            is MealItem.FoodItem -> TYPE_FOOD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_RECIPE -> {
                val binding = ItemSearchIngredientBinding.inflate(inflater, parent, false)
                RecipeViewHolder(binding.root)
            }
            else -> {
                val binding = ItemSearchIngredientBinding.inflate(inflater, parent, false)
                FoodViewHolder(binding.root)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is MealItem.RecipeItem -> (holder as RecipeViewHolder).bind(item.recipe)
            is MealItem.FoodItem -> (holder as FoodViewHolder).bind(item.food)
        }
    }

    override fun getItemCount(): Int = items.size

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(recipe: Recipe) {
            val binding = ItemSearchIngredientBinding.bind(itemView)
            binding.tvFoodName.text = recipe.name
            binding.tvCalories.text = recipe.calories.toString()
            binding.tvFoodDetails.text = "${recipe.servings} servings"
        }
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(food: Food) {
            val binding = ItemSearchIngredientBinding.bind(itemView)
            binding.tvFoodName.text = food.name
            binding.tvCalories.text = food.calories.toString()
            binding.tvFoodDetails.text = "${food.servings} servings"
        }
    }

    fun updateData(newItems: List<MealItem>) {
        if (items.isNotEmpty()) {
            items.clear()
        }
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}

