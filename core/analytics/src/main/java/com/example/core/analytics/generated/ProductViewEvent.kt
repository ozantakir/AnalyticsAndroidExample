// Generative AI tarafından otomatik üretilmiştir - Elle değiştirmeyiniz
package com.example.core.analytics.generated

import com.example.core.analytics.EventModel
import com.example.core.analytics.AnalyticsDestination

data class ProductViewEvent(
    val productId: String
) : EventModel() {

    override val eventName: String = "product_view"

    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
        AnalyticsDestination.FIREBASE -> mapOf(
            "item_id" to productId
        )
            else -> parameters
        }
    }
}
