// Generative AI tarafından otomatik üretilmiştir - Elle değiştirmeyiniz
package com.example.core.analytics.generated

import com.example.core.analytics.AnalyticsDestination
import com.example.core.analytics.AnalyticsKeys
import com.example.core.analytics.EventModel
import com.example.core.analytics.destination.AdjustEvent
import com.example.core.analytics.destination.FirebaseEvent
import com.example.core.analytics.destination.InsiderEvent

data class AddToCartClickedEvent(
    val productId: String,
    val quantity: Int
) : EventModel(), FirebaseEvent, InsiderEvent, AdjustEvent {

    override val eventName: String = "add_to_cart_clicked"

    override val contextKeys: List<String> = listOf(AnalyticsKeys.PRODUCT_DETAILS)

    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE, AnalyticsDestination.ADJUST, AnalyticsDestination.INSIDER
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId,
        "quantity" to quantity
    )

    override fun toFirebaseParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        val price = referenceData["p_price"] as? Double ?: 0.0
        val category = referenceData["p_segment"] as? String ?: "Unknown"
        return mapOf(
            "item_id" to productId,
            "value" to price,
            "quantity" to quantity,
            "item_category" to category
        )
    }

    override fun toInsiderParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        val price = referenceData["p_price"] as? Double ?: 0.0
        val category = referenceData["p_segment"] as? String ?: "Unknown"
        return mapOf(
            "product_id" to productId,
            "price" to price,
            "quantity" to quantity,
            "category" to category
        )
    }

    override fun toAdjustParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        val price = referenceData["p_price"] as? Double ?: 0.0
        return mapOf("revenue" to price)
    }
}
