package com.example.core.analytics

/**
 * Base model for all analytics events.
 * Follows GEMINI.md multi-destination and parameter mapping rules.
 */
abstract class EventModel {
    abstract val eventName: String
    
    // Base Raw Schema Parameters (Used for internal logging or SDKs without custom key mapping)
    abstract val parameters: Map<String, Any?>

    // Default to ALL destinations if not specified
    open val destinations: List<AnalyticsDestination> = listOf(AnalyticsDestination.ALL)

    /**
     * Transforms the event payload for a specific analytics provider.
     * Use "__action_type" key for native SDK method dispatching.
     */
    abstract fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?>
}

// Example concrete implementations
data class ProductViewEvent(
    val productId: String,
    val productName: String
) : EventModel() {
    override val eventName: String = "product_view"
    
    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE,
        AnalyticsDestination.MIXPANEL
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId,
        "productName" to productName
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
            AnalyticsDestination.FIREBASE -> mapOf(
                "item_id" to productId,
                "item_name" to productName
            )
            AnalyticsDestination.MIXPANEL -> mapOf(
                "Product ID" to productId,
                "Product Name" to productName
            )
            else -> parameters
        }
    }
}

data class AddToCartEvent(
    val productId: String,
    val productName: String,
    val price: Double
) : EventModel() {
    override val eventName: String = "add_to_cart"
    
    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE,
        AnalyticsDestination.INSIDER
    )

    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId,
        "productName" to productName,
        "price" to price
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
            AnalyticsDestination.FIREBASE -> mapOf(
                "item_id" to productId,
                "value" to price,
                "currency" to "USD"
            )
            AnalyticsDestination.INSIDER -> mapOf(
                "__action_type" to "CART_ADD",
                "product_id" to productId,
                "unit_price" to price
            )
            else -> parameters
        }
    }
}

