package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.LogWeight
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LogWeightFireStoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val collection = firestore.collection("log_weights")

    suspend fun addLogWeight(logWeight: LogWeight): String {
        val docRef = collection.document()
        val newLog = logWeight.copy(id = docRef.id)
        docRef.set(newLog).await()
        return docRef.id
    }

    suspend fun updateLogWeight(logWeight: LogWeight) {
        collection.document(logWeight.id).set(logWeight).await()
    }

    suspend fun deleteLogWeight(id: String) {
        collection.document(id).delete().await()
    }

    suspend fun getAllLogWeightsByUser(userId: String): List<LogWeight> {
        val snapshot = collection
            .whereEqualTo("userId", userId)
            .get()
            .await()
        return snapshot.toObjects(LogWeight::class.java)
    }

    suspend fun getLogWeightById(id: String): LogWeight? {
        val doc = collection.document(id).get().await()
        return if (doc.exists()) doc.toObject(LogWeight::class.java) else null
    }

}
