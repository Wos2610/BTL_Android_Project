package com.example.btl_android_project.presentation.log_recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemSearchIngredientBinding
import com.example.btl_android_project.local.entity.RecipeIngredient

class IngredientAdapter(
    private var ingredients: MutableList<RecipeIngredient>,
    private val onItemClick: (RecipeIngredient) -> Unit
) : RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemSearchIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val meal = ingredients[position]
        val binding = ItemSearchIngredientBinding.bind(holder.itemView)
        binding.tvFoodName.text = meal.description
        val caloriesPerOneServing = meal.foodNutrients.firstOrNull { it.name.contains("Energy") }?.amount?.toInt()

        binding.tvFoodDetails.text = "${meal.numberOfServings} x ${meal.servingSize} x ${caloriesPerOneServing} kcal"

        binding.tvCalories.text = caloriesPerOneServing?.times(meal.numberOfServings).toString() + " kcal"

        binding.root.setOnClickListener {
            onItemClick(meal)
        }
    }

    override fun getItemCount() = ingredients.size


    fun updateData(newIngredients: List<RecipeIngredient>) {
        if(ingredients.isNotEmpty()) {
            (ingredients as MutableList).clear()
            (ingredients as MutableList<RecipeIngredient>).addAll(newIngredients)
        }
        else{
            ingredients = newIngredients.toMutableList()
        }
        notifyDataSetChanged()
    }
}
