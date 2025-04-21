package com.example.btl_android_project.presentation.log_meal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Meal

class MealItemAdapter(
    private var meals: List<Meal>,
    private val onItemClick: (Meal) -> Unit,
    private val onAddToDiaryClick: (Meal) -> Unit,
) : RecyclerView.Adapter<MealItemAdapter.MealViewHolder>() {

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
        binding.btnAddMeal.setOnClickListener {
            onAddToDiaryClick(meal)
        }
        binding.root.setOnClickListener {
            onItemClick(meal)
        }
    }

    override fun getItemCount() = meals.size

    fun updateData(newMeals: List<Meal>) {
        if(meals.isNotEmpty()) {
            (meals as MutableList).clear()
            (meals as MutableList<Meal>).addAll(newMeals)
        }
        else{
            meals = newMeals
        }
        notifyDataSetChanged()
    }
}