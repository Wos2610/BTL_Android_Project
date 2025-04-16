package com.example.btl_android_project.presentation.log_recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemCheckIngredientBinding
import com.example.btl_android_project.databinding.ItemSearchIngredientBinding
import com.example.btl_android_project.local.entity.StaticRecipeIngredient

data class IngredientItem(
    val ingredient: StaticRecipeIngredient,
    var isChecked: Boolean = false
)

class CheckIngredientAdapter(
    private var ingredients: List<IngredientItem>,
    private val onItemClick: (StaticRecipeIngredient) -> Unit,
    private val onCheckChanged: (IngredientItem, Boolean) -> Unit
) : RecyclerView.Adapter<CheckIngredientAdapter.CheckIngredientViewHolder>() {

    class CheckIngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckIngredientViewHolder {
        val binding = ItemCheckIngredientBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckIngredientViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CheckIngredientViewHolder, position: Int) {
        val item = ingredients[position]
        val ingredient = item.ingredient
        val binding = ItemCheckIngredientBinding.bind(holder.itemView)

        binding.tvFoodName.text = ingredient.description

        ingredient.foodNutrients.firstOrNull { it.name.contains("Energy") }?.let {
            binding.tvCalories.text = it.amount.toInt().toString() + " kcal"
        }

        binding.checkbox.setOnCheckedChangeListener(null)
        binding.checkbox.isChecked = item.isChecked

        binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
            item.isChecked = isChecked
            onCheckChanged(item, isChecked)
        }

        binding.root.setOnClickListener {
            onItemClick(ingredient)
        }
    }

    override fun getItemCount() = ingredients.size

    fun updateData(newIngredients: List<StaticRecipeIngredient>) {
        val oldCheckedStates = ingredients.associate { it.ingredient.description to it.isChecked }

        val updatedList = newIngredients.map { ingredient ->
            IngredientItem(
                ingredient = ingredient,
                isChecked = oldCheckedStates[ingredient.description] ?: false
            )
        }

        ingredients = updatedList
        notifyDataSetChanged()
    }

    fun checkAll() {
        ingredients.forEach { it.isChecked = true }
        notifyDataSetChanged()
    }

    fun uncheckAll() {
        ingredients.forEach { it.isChecked = false }
        notifyDataSetChanged()
    }

    fun getCheckedIngredients(): List<StaticRecipeIngredient> {
        return ingredients.filter { it.isChecked }.map { it.ingredient }
    }
}