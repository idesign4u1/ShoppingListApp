package com.shoppinglist.app.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class ChatMessage(
    val id: String = "",
    val listId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", null, 0)
}

@Singleton
class ChatRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {
    private val chatCollection = firestore.collection("chats")

    fun getMessages(listId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = chatCollection
            .whereEqualTo("listId", listId)
            // .orderBy("timestamp", Query.Direction.ASCENDING) // Client side sort to match other fixes
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(ChatMessage::class.java)
                }?.sortedBy { it.timestamp } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendMessage(listId: String, text: String, imageUrl: String? = null): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Not logged in")
            val docRef = chatCollection.document()
            val message = ChatMessage(
                id = docRef.id,
                listId = listId,
                senderId = user.uid,
                senderName = user.displayName ?: user.email ?: "Anonymous",
                text = text,
                imageUrl = imageUrl,
                timestamp = System.currentTimeMillis()
            )
            docRef.set(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Placeholder for image upload - requires Firebase Storage
    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val uuid = java.util.UUID.randomUUID().toString()
            val ref = storage.reference.child("chat_images/$uuid")
            ref.putFile(uri).await()
            val downloadUrl = ref.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
