package com.shoppinglist.app.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", 0)
}
