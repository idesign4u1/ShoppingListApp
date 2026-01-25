package com.shoppinglist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    
    suspend fun getCustomCategories(): Result<List<String>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.success(emptyList())
            val snapshot = firestore.collection("users").document(userId)
                .collection("categories")
                .get()
                .await()
            
            val categories = snapshot.documents.mapNotNull { it.getString("name") }
            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addCustomCategory(category: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val data = mapOf("name" to category)
            
            firestore.collection("users").document(userId)
                .collection("categories")
                .document(category) // Use name as ID to prevent duplicates
                .set(data)
                .await()
                
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
