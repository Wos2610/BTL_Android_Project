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
    var heightCm: Double? = null,
    val currentWeight: Double? = null,
    val goalWeight: Double? = null,
) : Parcelable
