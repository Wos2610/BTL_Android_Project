package com.example.btl_android_project.presentation.log_food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.databinding.ItemMealBinding
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.presentation.log_all.LogAllFragmentDirections

class FoodAdapter(private var foods: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        val binding = ItemMealBinding.bind(holder.itemView)
        binding.tvMealName.text = food.name
        binding.tvMealInfo.text = food.calories.toString()

        holder.itemView.setOnClickListener {
            val action =
                LogAllFragmentDirections.actionLogAllFragmentToCreateFoodInformationFragment(food.id)
            it.findNavController().navigate(action)
        }

    }

    fun updateFoods(newFoods: List<Food>) {
        foods = newFoods
        notifyDataSetChanged()
    }

    fun getFoods(): List<Food> = foods

    override fun getItemCount() = foods.size
}