package com.example.btl_android_project.local.entity

import java.io.Serializable

data class User(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val password: String = "",
) : Serializable