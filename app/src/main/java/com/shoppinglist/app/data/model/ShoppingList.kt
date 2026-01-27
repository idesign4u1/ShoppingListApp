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
    val updatedAt: Long = System.currentTimeMillis(),
    
    // Shopping features - Phase 6
    val budget: Double? = null,
    val totalSpent: Double = 0.0,
    val estimatedTotal: Double = 0.0,
    val currency: String = "₪",
    
    // Advanced features - Phase 4
    val icon: String? = null, // Emoji or icon identifier
    val color: String? = null, // Custom color
    val isTemplate: Boolean = false,
    val templateCategory: String? = null
) {
    constructor() : this(
        "", "", "", "", "", emptyList(), emptyList(), 0, 0, 0, 0,
        null, 0.0, 0.0, "₪", null, null, false, null
    )
    
    /**
     * Calculate budget usage percentage
     */
    fun getBudgetUsagePercentage(): Float {
        return budget?.let { 
            if (it > 0) (totalSpent / it * 100).toFloat() 
            else 0f 
        } ?: 0f
    }
    
    /**
     * Check if budget is exceeded
     */
    fun isBudgetExceeded(): Boolean {
        return budget?.let { totalSpent > it } ?: false
    }
    
    /**
     * Get remaining budget
     */
    fun getRemainingBudget(): Double {
        return budget?.let { it - totalSpent } ?: 0.0
    }
    
    /**
     * Get budget status
     */
    fun getBudgetStatus(): BudgetStatus {
        val percentage = getBudgetUsagePercentage()
        return when {
            budget == null -> BudgetStatus.NO_BUDGET
            percentage >= 100 -> BudgetStatus.EXCEEDED
            percentage >= 80 -> BudgetStatus.WARNING
            else -> BudgetStatus.GOOD
        }
    }
    
    /**
     * Get completion percentage
     */
    fun getCompletionPercentage(): Float {
        return if (itemCount > 0) (completedCount.toFloat() / itemCount * 100) else 0f
    }
}

/**
 * Budget status for visual indicators
 */
enum class BudgetStatus {
    NO_BUDGET,
    GOOD,
    WARNING,
    EXCEEDED
}
