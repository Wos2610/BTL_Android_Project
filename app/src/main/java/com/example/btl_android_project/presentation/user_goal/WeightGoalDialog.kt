package com.example.btl_android_project.presentation.user_goal

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.example.btl_android_project.R
import com.example.btl_android_project.local.enums.ActivityLevel
import com.example.btl_android_project.local.enums.WeightGoal
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeightGoalDialog(
    private val context: Context,
    private val userProfile: UserProfileArgument,
    private val onSaveCallback: (UserProfileArgument) -> Unit
) {
    private lateinit var dialog: AlertDialog
    private lateinit var updatedProfile: UserProfileArgument
    
    fun show() {
        // Copy the current profile to work with
        updatedProfile = userProfile.copy()
        
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_weight_goal, null)
        
        setupViews(view)
        
        builder.setView(view)
        builder.setCancelable(true)
        
        dialog = builder.create()
        dialog.show()
    }
    
    private fun setupViews(view: View) {
        // Initialize form elements with current values
        val etCurrentWeight = view.findViewById<EditText>(R.id.etCurrentWeight)
        val etGoalWeight = view.findViewById<EditText>(R.id.etGoalWeight)
        val etHeight = view.findViewById<EditText>(R.id.etHeight)
        val tvDateOfBirth = view.findViewById<TextView>(R.id.tvDateOfBirth)
        val rgGender = view.findViewById<RadioGroup>(R.id.rgGender)
        val rbMale = view.findViewById<RadioButton>(R.id.rbMale)
        val rbFemale = view.findViewById<RadioButton>(R.id.rbFemale)
        val etCity = view.findViewById<EditText>(R.id.etCity)
        val spinnerWeightGoal = view.findViewById<Spinner>(R.id.spinnerWeightGoal)
        val spinnerActivityLevel = view.findViewById<Spinner>(R.id.spinnerActivityLevel)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        
        // Set up weight goal spinner
        val weightGoals = arrayOf(WeightGoal.LOSE_WEIGHT.name, WeightGoal.MAINTAIN_WEIGHT.name, WeightGoal.GAIN_WEIGHT.name)
        val weightGoalAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, weightGoals)
        weightGoalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeightGoal.adapter = weightGoalAdapter
        
        // Set default selection for weight goal
        val weightGoalPosition = weightGoals.indexOf(userProfile.weightGoal)
        if (weightGoalPosition != -1) {
            spinnerWeightGoal.setSelection(weightGoalPosition)
        }
        
        // Set up activity level spinner
        val activityLevels = arrayOf(ActivityLevel.NOT_ACTIVE.name, ActivityLevel.LIGHT_ACTIVE.name, ActivityLevel.ACTIVE.name, ActivityLevel.VERY_ACTIVE.name)
        val activityAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, activityLevels)
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerActivityLevel.adapter = activityAdapter
        
        // Set default selection for activity level
        val activityPosition = activityLevels.indexOf(userProfile.activityLevel)
        if (activityPosition != -1) {
            spinnerActivityLevel.setSelection(activityPosition)
        }
        
        // Fill in current values
        etCurrentWeight.setText(userProfile.currentWeight?.toString() ?: "")
        etGoalWeight.setText(userProfile.goalWeight?.toString() ?: "")
        etHeight.setText(userProfile.heightCm?.toString() ?: "")
        tvDateOfBirth.text = userProfile.dateOfBirth ?: "Select date of birth"
        etCity.setText(userProfile.city ?: "")
        
        // Set gender selection
        if (userProfile.isFeMale == true) {
            rbFemale.isChecked = true
        } else {
            rbMale.isChecked = true
        }
        
        // Handle date of birth picker
        tvDateOfBirth.setOnClickListener {
            showDatePicker(tvDateOfBirth)
        }
        
        // Handle spinner selection changes
        spinnerWeightGoal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updatedProfile = updatedProfile.copy(weightGoal = weightGoals[position])
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        
        spinnerActivityLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updatedProfile = updatedProfile.copy(activityLevel = activityLevels[position])
            }
            
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        
        // Handle Save button click
        btnSave.setOnClickListener {
            if (validateInputs(etCurrentWeight, etGoalWeight, etHeight)) {
                // Update the profile with form values
                val currentWeight = etCurrentWeight.text.toString().toFloatOrNull()
                val goalWeight = etGoalWeight.text.toString().toFloatOrNull()
                val height = etHeight.text.toString().toFloatOrNull()
                val isFemale = rbFemale.isChecked
                val city = etCity.text.toString().trim()
                
                updatedProfile = updatedProfile.copy(
                    currentWeight = currentWeight,
                    goalWeight = goalWeight,
                    heightCm = height,
                    isFeMale = isFemale,
                    city = if (city.isNotEmpty()) city else null
                )
                
                // Call the callback with updated profile
                onSaveCallback(updatedProfile)
                dialog.dismiss()
            }
        }
        
        // Handle Cancel button click
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }
    
    private fun validateInputs(
        etCurrentWeight: EditText,
        etGoalWeight: EditText,
        etHeight: EditText
    ): Boolean {
        var isValid = true
        
        if (etCurrentWeight.text.toString().trim().isEmpty()) {
            etCurrentWeight.error = "Current weight is required"
            isValid = false
        }
        
        if (etGoalWeight.text.toString().trim().isEmpty()) {
            etGoalWeight.error = "Goal weight is required"
            isValid = false
        }
        
        if (etHeight.text.toString().trim().isEmpty()) {
            etHeight.error = "Height is required"
            isValid = false
        }
        
        return isValid
    }
    
    private fun showDatePicker(tvDateOfBirth: TextView) {
        val activity = context as? FragmentActivity ?: return
        
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date of Birth")
            .build()
        
        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = sdf.format(Date(selection))
            tvDateOfBirth.text = date
            updatedProfile = updatedProfile.copy(dateOfBirth = date)
        }
        
        datePicker.show(activity.supportFragmentManager, "DATE_PICKER")
    }
}