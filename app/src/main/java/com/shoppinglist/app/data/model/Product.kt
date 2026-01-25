package com.shoppinglist.app.data.model

data class Product(
    val id: String = "",
    val listId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val unit: String = "יחידות",
    val category: String = "כללי",
    val notes: String = "",
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val completedAt: Long? = null,
    val addedBy: String = "",
    val addedByEmail: String = "",
    val assignedTo: String? = null,
    val assignedToName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", 1, "יחידות", "כללי", "", false, null, null, "", "", null, null, 0, 0)
}

object ProductCategories {
    val list = listOf(
        "כללי",
        "ירקות ופירות",
        "מוצרי חלב",
        "בשר ודגים",
        "מאפים ולחם",
        "שתייה",
        "חטיפים וממתקים",
        "מוצרי ניקיון",
        "טיפוח אישי",
        "קפואים",
        "שימורים",
        "תבלינים"
    )
}

object ProductUnits {
    val list = listOf(
        "יחידות",
        "ק\"ג",
        "גרם",
        "ליטר",
        "מ\"ל",
        "חבילות",
        "קופסאות",
        "בקבוקים",
        "שקיות"
    )
}
