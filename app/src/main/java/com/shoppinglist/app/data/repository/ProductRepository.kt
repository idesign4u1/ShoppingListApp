package com.shoppinglist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shoppinglist.app.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val productsCollection = firestore.collection("products")

    fun getProductsForList(listId: String): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .whereEqualTo("listId", listId)
            // .orderBy("isCompleted") // Moved to client side
            // .orderBy("createdAt", Query.Direction.DESCENDING) // Moved to client side
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }?.sortedWith(
                    compareBy<Product> { it.isCompleted }
                        .thenByDescending { it.createdAt }
                ) ?: emptyList()
                trySend(products)
            }
        awaitClose { listener.remove() }
    }

    suspend fun addProduct(
        listId: String,
        name: String,
        quantity: Int,
        unit: String,
        category: String,
        notes: String
    ): Result<Product> {
        return try {
            val user = auth.currentUser ?: throw Exception("משתמש לא מחובר")
            val docRef = productsCollection.document()

            val product = Product(
                id = docRef.id,
                listId = listId,
                name = name,
                quantity = quantity,
                unit = unit,
                category = category,
                notes = notes,
                addedBy = user.uid,
                addedByEmail = user.email ?: ""
            )

            docRef.set(product).await()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            productsCollection.document(product.id).set(
                product.copy(updatedAt = System.currentTimeMillis())
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleProductCompletion(product: Product): Result<Unit> {
        return try {
            val user = auth.currentUser
            val updates = if (product.isCompleted) {
                mapOf(
                    "isCompleted" to false,
                    "completedBy" to null,
                    "completedAt" to null,
                    "updatedAt" to System.currentTimeMillis()
                )
            } else {
                mapOf(
                    "isCompleted" to true,
                    "completedBy" to user?.uid,
                    "completedAt" to System.currentTimeMillis(),
                    "updatedAt" to System.currentTimeMillis()
                )
            }
            productsCollection.document(product.id).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCompletedProducts(listId: String): Result<Int> {
        return try {
            val completed = productsCollection
                .whereEqualTo("listId", listId)
                .whereEqualTo("isCompleted", true)
                .get()
                .await()

            val batch = firestore.batch()
            completed.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
            batch.commit().await()

            Result.success(completed.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
