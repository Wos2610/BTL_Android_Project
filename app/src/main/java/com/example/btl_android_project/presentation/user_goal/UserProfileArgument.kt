package com.example.btl_android_project.presentation.user_goal

import android.os.Parcelable

import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfileArgument(
    val weightGoal: String? = null,
    val activityLevel: String? = null,
    val dateOfBirth: String? = null,
    val city: String? = null,
    val isFeMale: Boolean? = null,
    var heightCm: Float? = null,
    val currentWeight: Float? = null,
    val goalWeight: Float? = null,
    val goalWater: Float? = null,
    val caloriesGoal: Int? = null,
) : Parcelable
