package com.example.btl_android_project.presentation.log_recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemSearchIngredientBinding
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipeIngredient

class StaticIngredientAdapter(
    private var ingredients: List<StaticRecipeIngredient>,
    private val onItemClick: (StaticRecipeIngredient) -> Unit
) : RecyclerView.Adapter<StaticIngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemSearchIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val meal = ingredients[position]
        val binding = ItemSearchIngredientBinding.bind(holder.itemView)
        binding.tvFoodName.text = meal.description
        meal.foodNutrients.firstOrNull { it.name.contains("Energy") }?.let {
            binding.tvCalories.text = it.amount.toInt().toString() + " kcal"
        }

        binding.root.setOnClickListener {
            onItemClick(meal)
        }
    }

    override fun getItemCount() = ingredients.size

    fun updateData(newIngredients: List<StaticRecipeIngredient>) {
        if(ingredients.isNotEmpty()) {
            (ingredients as MutableList).clear()
            (ingredients as MutableList<StaticRecipeIngredient>).addAll(newIngredients)
        }
        else{
            ingredients = newIngredients
        }
        notifyDataSetChanged()
    }
}
