package com.shoppinglist.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.shoppinglist.app.data.model.Invitation
import com.shoppinglist.app.data.model.InvitationStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvitationRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val invitationsCollection = firestore.collection("invitations")

    fun getMyInvitations(): Flow<List<Invitation>> = callbackFlow {
        val userEmail = auth.currentUser?.email ?: return@callbackFlow

        val listener = invitationsCollection
            .whereEqualTo("inviteeEmail", userEmail)
            .whereEqualTo("status", InvitationStatus.PENDING.name)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val invitations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Invitation::class.java)?.copy(
                        status = InvitationStatus.valueOf(
                            doc.getString("status") ?: InvitationStatus.PENDING.name
                        )
                    )
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(invitations)
            }
        awaitClose { listener.remove() }
    }

    suspend fun sendInvitation(
        listId: String,
        listName: String,
        inviteeEmail: String
    ): Result<Invitation> {
        return try {
            val user = auth.currentUser ?: throw Exception("משתמש לא מחובר")

            val existing = invitationsCollection
                .whereEqualTo("listId", listId)
                .whereEqualTo("inviteeEmail", inviteeEmail)
                .whereEqualTo("status", InvitationStatus.PENDING.name)
                .get()
                .await()

            if (existing.documents.isNotEmpty()) {
                throw Exception("הזמנה כבר נשלחה למשתמש זה")
            }

            val docRef = invitationsCollection.document()
            val invitation = Invitation(
                id = docRef.id,
                listId = listId,
                listName = listName,
                inviterEmail = user.email ?: "",
                inviterName = user.displayName ?: user.email ?: "",
                inviteeEmail = inviteeEmail
            )

            val data = mapOf(
                "id" to invitation.id,
                "listId" to invitation.listId,
                "listName" to invitation.listName,
                "inviterEmail" to invitation.inviterEmail,
                "inviterName" to invitation.inviterName,
                "inviteeEmail" to invitation.inviteeEmail,
                "status" to invitation.status.name,
                "createdAt" to invitation.createdAt
            )
            docRef.set(data).await()

            Result.success(invitation)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptInvitation(invitation: Invitation): Result<Unit> {
        return try {
            invitationsCollection.document(invitation.id).update(
                "status", InvitationStatus.ACCEPTED.name
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun declineInvitation(invitation: Invitation): Result<Unit> {
        return try {
            invitationsCollection.document(invitation.id).update(
                "status", InvitationStatus.DECLINED.name
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
