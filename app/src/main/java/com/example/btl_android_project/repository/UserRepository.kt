package com.example.btl_android_project.repository

import com.example.btl_android_project.local.entity.User
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class UserRepository @Inject constructor() {
    fun addUser(userId: Int, username: String, email: String, password: String) {
        val db = FirebaseFirestore.getInstance()
        val user = User(userId, username, email, password)

        db.collection("users")
            .document(userId.toString())
            .set(user)
            .addOnSuccessListener {
                Timber.d("User added successfully with ID: $userId")
            }
            .addOnFailureListener { e ->
                Timber.e(e, "Error adding user document")
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("User added successfully with ID: $userId")
                } else {
                    Timber.e(task.exception, "Error adding user document")
                }
            }
    }


}