package com.example.btl_android_project.presentation.log_food

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.btl_android_project.R
import com.example.btl_android_project.local.entity.Nutrition

class NutritionAdapter(
    private var nutritionList: List<Nutrition>,
    private val onAmountChanged: (Int, Float) -> Unit
) : RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder>() {

    inner class NutritionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNutritionName: TextView = itemView.findViewById(R.id.tvNutritionName)
        val etNutritionAmount: EditText = itemView.findViewById(R.id.etNutritionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_food_nutrition, parent, false)
        return NutritionViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: NutritionViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val nutrition = nutritionList[position]
        holder.tvNutritionName.text = nutrition.name + " ( " + nutrition.unitName + " )"
        holder.etNutritionAmount.setText(nutrition.amount.toString())

        holder.etNutritionAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amount = s.toString().toFloatOrNull() ?: 0f
                onAmountChanged(position, amount)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = nutritionList.size

    fun updateList(newList: List<Nutrition>) {
        nutritionList = newList
        notifyDataSetChanged()
    }
}
