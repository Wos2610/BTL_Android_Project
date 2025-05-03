package com.example.btl_android_project.presentation.diary

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.btl_android_project.R

class CaloriesSummaryView @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null, 
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val goalTextView: TextView
    private val foodTextView: TextView
    private val exerciseTextView: TextView
    private val remainingTextView: TextView
    
    init {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.view_calories_summary, this, true)
        
        goalTextView = findViewById(R.id.goalTextView)
        foodTextView = findViewById(R.id.foodTextView)
        exerciseTextView = findViewById(R.id.exerciseTextView)
        remainingTextView = findViewById(R.id.remainingTextView)
        
        // Set default values
        setValues(1440, 10, 0, 1430)
    }
    
    fun setValues(goal: Int, food: Int, exercise: Int, remaining: Int) {
        goalTextView.text = goal.toString()
        foodTextView.text = food.toString()
        exerciseTextView.text = exercise.toString()
        remainingTextView.text = remaining.toString()
    }
}