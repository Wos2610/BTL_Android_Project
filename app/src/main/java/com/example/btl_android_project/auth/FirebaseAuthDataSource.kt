package com.example.btl_android_project.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import javax.inject.Inject

class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    fun registerUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserToFirestore(user?.uid)
                    onSuccess()
                } else {
                    Timber.e("Registration failed: ${task.exception}")
                    onFailure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    private fun saveUserToFirestore(userId: String?) {
        if (userId == null) return

        val user = hashMapOf(
            "userId" to userId,
            "email" to auth.currentUser?.email,
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener {
                Timber.d("Save user to Firestore successfully: $userId")
            }
            .addOnFailureListener { e ->
                Timber.e("Error saving user to Firestore: $e")
            }
    }

    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception ?: Exception("Unknown error"))
                }
            }
    }

    fun logoutUser(
        navigateToLoginScreen: () -> Unit
    ) {
        auth.signOut()
        navigateToLoginScreen()
    }

    fun checkUserLoggedIn(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            onSuccess()
        } else {
            onFailure(Exception("User not logged in"))
        }
    }

    fun getCurrentUserId(): String? {
        Timber.d("Current user ID: ${auth.currentUser?.uid}")
        return auth.currentUser?.uid
    }
}