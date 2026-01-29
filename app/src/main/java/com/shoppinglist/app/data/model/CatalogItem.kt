package com.shoppinglist.app.data.model

data class CatalogItem(
    val id: String = "",
    val name: String = "",
    val category: String = "כללי",
    val defaultUnit: String = "יח'",
    val estimatedPrice: Double? = null,
    val popularity: Int = 0 // use to sort suggestions
)
