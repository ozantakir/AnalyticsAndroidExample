package com.example.core.analytics.transformer

import com.example.core.analytics.context.DynamicContext
import com.example.core.analytics.store.AnalyticsStore
import com.example.core.domain.model.Product

/**
 * Transforms a Domain Product model into an AnalyticsContext with modifications.
 */
class ProductDomainTransformer : AnalyticsTransformer<Product> {
    override fun transform(input: Product, store: AnalyticsStore): DynamicContext {
        // Complex modification example: Calculate segment based on price
        val priceSegment = if (input.price > 1000) "High-Value" else "Normal"
        
        // Example of reading from store: Imagine currency was set by another service earlier
        val currency = "TRY" // In a real app, this could be store.getContext("Currency") as? ...

        return DynamicContext(
            schemaName = "product_context",
            data = mapOf(
                "p_name" to input.name,
                "p_price" to input.price,
                "p_segment" to priceSegment,
                "p_currency" to currency,
                "p_is_sale" to (input.price < 500.0)
            )
        )
    }
}
