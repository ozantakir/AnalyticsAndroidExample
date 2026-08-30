// Generative AI tarafından otomatik üretilmiştir - Elle değiştirmeyiniz
package com.example.analytics.generated

import com.example.analytics.EventModel
import com.example.analytics.AnalyticsDestination

data class AddToCartClickedEvent(
    val productId: String,
    val price: Double,
    val quantity: Int,
    val category: String? = null
) : EventModel() {

    override val eventName: String = "add_to_cart_clicked"

    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE, AnalyticsDestination.ADJUST, AnalyticsDestination.SGTM
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId,
        "price" to price,
        "quantity" to quantity,
        "category" to category
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
        AnalyticsDestination.FIREBASE -> mapOf(
            "item_id" to productId,
            "value" to price,
            "quantity" to quantity,
            "item_category" to category
        )
        AnalyticsDestination.ADJUST -> mapOf(
            "revenue" to price
        )
        AnalyticsDestination.SGTM -> mapOf(
            "product_id" to productId,
            "price" to price,
            "quantity" to quantity,
            "category" to category
        )
            else -> parameters
        }
    }
}
