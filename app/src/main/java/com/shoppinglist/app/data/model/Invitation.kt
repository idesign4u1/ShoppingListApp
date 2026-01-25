package com.shoppinglist.app.data.model

data class Invitation(
    val id: String = "",
    val listId: String = "",
    val listName: String = "",
    val inviterEmail: String = "",
    val inviterName: String = "",
    val inviteeEmail: String = "",
    val status: InvitationStatus = InvitationStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", "", InvitationStatus.PENDING, 0)
}

enum class InvitationStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}
