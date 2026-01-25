package com.shoppinglist.app.data.model

data class ShoppingList(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val ownerId: String = "",
    val ownerEmail: String = "",
    val members: List<String> = emptyList(),
    val memberEmails: List<String> = emptyList(),
    val itemCount: Int = 0,
    val completedCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", emptyList(), emptyList(), 0, 0, 0, 0)
}
