package com.example.btl_android_project.presentation.log_exercise

interface SearchableExerciseList {
    fun search(query: String)
    fun loadDataAgain()
}