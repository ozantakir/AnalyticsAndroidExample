// Generative AI tarafından otomatik üretilmiştir - Elle değiştirmeyiniz
package com.example.core.analytics.generated

import com.example.core.analytics.AnalyticsDestination
import com.example.core.analytics.AnalyticsKeys
import com.example.core.analytics.EventModel
import com.example.core.analytics.destination.FirebaseEvent

data class ProductViewEvent(
    val productId: String
) : EventModel(), FirebaseEvent {

    override val eventName: String = "product_view"

    /**
     * Store'daki anahtarları referans alır.
     */
    override val contextKeys: List<String> = listOf(AnalyticsKeys.PRODUCT_DETAILS)

    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId
    )

    override fun toFirebaseParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        // Store'dan gelen modifiye edilmiş verileri alıyoruz
        val price = referenceData["p_price"] as? Double ?: 0.0

        return mapOf(
            "item_id" to productId,
            "value" to price
        )
    }
}
