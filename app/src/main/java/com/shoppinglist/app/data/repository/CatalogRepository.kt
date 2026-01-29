package com.shoppinglist.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.shoppinglist.app.data.model.CatalogItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val catalogCollection = firestore.collection("catalog")

    suspend fun searchCatalog(query: String): List<CatalogItem> {
        if (query.length < 2) return emptyList()
        
        // Simple search: finding items where name starts with query
        // Firestore key sorting trick for prefix search
        val endQuery = query + "\uF7FF"
        
        return try {
            catalogCollection
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", endQuery)
                .limit(5)
                .get()
                .await()
                .toObjects(CatalogItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun seedInitialDataIfNeeded() {
        if (catalogCollection.limit(1).get().await().isEmpty) {
            val initialItems = listOf(
                CatalogItem(name = "חלב תנובה 3%", category = "מוצרי חלב", estimatedPrice = 6.20),
                CatalogItem(name = "קוטג' תנובה 5%", category = "מוצרי חלב", estimatedPrice = 5.90),
                CatalogItem(name = "לחם אחיד פרוס", category = "מאפים ולחם", estimatedPrice = 7.10),
                CatalogItem(name = "לחם מלא", category = "מאפים ולחם", estimatedPrice = 12.90),
                CatalogItem(name = "ביצים L (תבנית 12)", category = "ביצים", estimatedPrice = 13.50),
                CatalogItem(name = "ביצים M (תבנית 30)", category = "ביצים", estimatedPrice = 28.90),
                CatalogItem(name = "עגבניה (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 5.90, defaultUnit = "ק\"ג"),
                CatalogItem(name = "מלפפון (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 4.90, defaultUnit = "ק\"ג"),
                CatalogItem(name = "בצל יבש (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 3.90, defaultUnit = "ק\"ג"),
                CatalogItem(name = "תפוח אדמה (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 4.50, defaultUnit = "ק\"ג"),
                CatalogItem(name = "בננות (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 6.90, defaultUnit = "ק\"ג"),
                CatalogItem(name = "תפוח עץ (ק\"ג)", category = "ירקות ופירות", estimatedPrice = 9.90, defaultUnit = "ק\"ג"),
                CatalogItem(name = "חזה עוף טרי (ק\"ג)", category = "בשר ודגים", estimatedPrice = 35.00, defaultUnit = "ק\"ג"),
                CatalogItem(name = "בשר טחון טרי (ק\"ג)", category = "בשר ודגים", estimatedPrice = 55.00, defaultUnit = "ק\"ג"),
                CatalogItem(name = "קוקה קולה (1.5 ליטר)", category = "שתייה", estimatedPrice = 7.50),
                CatalogItem(name = "מים מינרלים (שישייה)", category = "שתייה", estimatedPrice = 12.00),
                CatalogItem(name = "קמח לבן (1 ק\"ג)", category = "מוצרי יסוד", estimatedPrice = 4.50),
                CatalogItem(name = "סוכר לבן (1 ק\"ג)", category = "מוצרי יסוד", estimatedPrice = 4.90),
                CatalogItem(name = "שמן קנולה (1 ליטר)", category = "מוצרי יסוד", estimatedPrice = 9.90),
                CatalogItem(name = "אורז פרסי (1 ק\"ג)", category = "מוצרי יסוד", estimatedPrice = 8.90),
                CatalogItem(name = "פסטה (500 גרם)", category = "מוצרי יסוד", estimatedPrice = 5.50),
                CatalogItem(name = "רסק עגבניות (מארז)", category = "מוצרי יסוד", estimatedPrice = 12.90),
                CatalogItem(name = "קפה נמס (200 גרם)", category = "קפה ותה", estimatedPrice = 24.90),
                CatalogItem(name = "נייר טואלט (32 גלילים)", category = "ניקיון ופארם", estimatedPrice = 34.90),
                CatalogItem(name = "אקונומיקה (4 ליטר)", category = "ניקיון ופארם", estimatedPrice = 12.90),
                CatalogItem(name = "נוזל כלים (750 מ\"ל)", category = "ניקיון ופארם", estimatedPrice = 8.90),
                CatalogItem(name = "שמפו (700 מ\"ל)", category = "ניקיון ופארם", estimatedPrice = 14.90),
                CatalogItem(name = "מרכך כביסה (4 ליטר)", category = "ניקיון ופארם", estimatedPrice = 19.90)
            )

            val batch = firestore.batch()
            initialItems.forEach { item ->
                val docRef = catalogCollection.document() 
                // Assign generated ID to item
                batch.set(docRef, item.copy(id = docRef.id))
            }
            batch.commit().await()
        }
    }
}
