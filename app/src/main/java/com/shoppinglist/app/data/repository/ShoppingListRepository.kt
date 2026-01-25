package com.shoppinglist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shoppinglist.app.data.model.ShoppingList
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingListRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val listsCollection = firestore.collection("shoppingLists")

    fun getMyLists(): Flow<List<ShoppingList>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow

        val listener = listsCollection
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lists = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ShoppingList::class.java)
                }?.sortedByDescending { it.updatedAt } ?: emptyList()
                trySend(lists)
            }
        awaitClose { listener.remove() }
    }

    fun getListById(listId: String): Flow<ShoppingList?> = callbackFlow {
        val listener = listsCollection.document(listId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val list = snapshot?.toObject(ShoppingList::class.java)
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createList(name: String, description: String): Result<ShoppingList> {
        return try {
            val user = auth.currentUser ?: throw Exception("משתמש לא מחובר")
            val docRef = listsCollection.document()

            val list = ShoppingList(
                id = docRef.id,
                name = name,
                description = description,
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                members = listOf(user.uid),
                memberEmails = listOf(user.email ?: "")
            )

            docRef.set(list).await()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateList(list: ShoppingList): Result<Unit> {
        return try {
            listsCollection.document(list.id).set(
                list.copy(updatedAt = System.currentTimeMillis())
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteList(listId: String): Result<Unit> {
        return try {
            // Delete all products in the list
            val products = firestore.collection("products")
                .whereEqualTo("listId", listId)
                .get()
                .await()

            val batch = firestore.batch()
            products.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.delete(listsCollection.document(listId))
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMember(listId: String, email: String, userId: String): Result<Unit> {
        return try {
            listsCollection.document(listId).update(
                mapOf(
                    "members" to FieldValue.arrayUnion(userId),
                    "memberEmails" to FieldValue.arrayUnion(email),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeMember(listId: String, email: String, userId: String): Result<Unit> {
        return try {
            listsCollection.document(listId).update(
                mapOf(
                    "members" to FieldValue.arrayRemove(userId),
                    "memberEmails" to FieldValue.arrayRemove(email),
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItemCounts(listId: String, total: Int, completed: Int) {
        try {
            listsCollection.document(listId).update(
                mapOf(
                    "itemCount" to total,
                    "completedCount" to completed
                )
            ).await()
        } catch (_: Exception) {
        }
    }

    suspend fun duplicateList(originalListId: String, newName: String): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("משתמש לא מחובר")
            
            // 1. Get original list
            val originalListSnapshot = listsCollection.document(originalListId).get().await()
            val originalList = originalListSnapshot.toObject(ShoppingList::class.java) 
                ?: throw Exception("הרשימה המקורית לא נמצאה")

            // 2. Create new list
            val newListRef = listsCollection.document()
            val newList = originalList.copy(
                id = newListRef.id,
                name = newName,
                ownerId = user.uid,
                ownerEmail = user.email ?: "",
                members = listOf(user.uid),
                memberEmails = listOf(user.email ?: ""),
                completedCount = 0,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            newListRef.set(newList).await()

            // 3. Get products and duplicate them
            val productsSnapshot = firestore.collection("products")
                .whereEqualTo("listId", originalListId)
                .get()
                .await()

            val batch = firestore.batch()
            var count = 0
            
            productsSnapshot.documents.forEach { doc ->
                val product = doc.toObject(com.shoppinglist.app.data.model.Product::class.java)
                if (product != null) {
                    val newProductRef = firestore.collection("products").document()
                    val newProduct = product.copy(
                        id = newProductRef.id,
                        listId = newListRef.id,
                        isCompleted = false,
                        completedBy = null,
                        completedAt = null,
                        addedBy = user.uid,
                        addedByEmail = user.email ?: "",
                        // Do not copy assignments for now, or maybe yes? Let's clear assignment on copy for simplicity unless requested.
                        // User asked "duplicate lists to a NEW list that was NOT bought". So assignments might be relevant but typically fresh start.
                        assignedTo = null,
                        assignedToName = null,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    batch.set(newProductRef, newProduct)
                    count++
                }
            }
            
            // Update item count for the new list
            batch.update(newListRef, "itemCount", count)
            
            batch.commit().await()
            Result.success(newListRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
