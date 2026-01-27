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
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Shopping features - Phase 6
    val price: Double? = null,
    val currency: String = "₪",
    val estimatedPrice: Double? = null, // For budget planning
    
    // Collaboration features - Phase 3
    val claimedBy: String? = null, // User who claimed "I'll buy this"
    val claimedByName: String? = null,
    val claimedAt: Long? = null,
    val status: ProductStatus = ProductStatus.PENDING,
    
    // Advanced features - Phase 4
    val imageUrl: String? = null,
    val barcode: String? = null,
    
    // Smart features - Phase 2
    val purchaseFrequency: Int = 0, // How many times purchased
    val lastPurchasedAt: Long? = null,
    val suggestedCategory: String? = null
) {
    constructor() : this(
        "", "", "", 1, "יחידות", "כללי", "", false, null, null, "", "", null, null, 0, 0,
        null, "₪", null, null, null, null, ProductStatus.PENDING, null, null, 0, null, null
    )
    
    /**
     * Calculate total price for this product
     */
    fun getTotalPrice(): Double? {
        return price?.times(quantity)
    }
    
    /**
     * Get display price string
     */
    fun getDisplayPrice(): String? {
        return price?.let { "$currency${String.format("%.2f", it)}" }
    }
    
    /**
     * Check if product is claimed by someone
     */
    fun isClaimed(): Boolean = claimedBy != null
}

/**
 * Product status for collaboration
 */
enum class ProductStatus {
    PENDING,      // Not claimed by anyone
    CLAIMED,      // Someone said "I'll buy this"
    IN_PROGRESS,  // Being purchased
    COMPLETED     // Purchased
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
