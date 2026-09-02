package com.example.core.analytics.transformer

import com.example.core.analytics.context.DynamicContext
import com.example.core.analytics.store.AnalyticsStore
import com.example.core.network.model.ProductResponseDto

class ProductTransformer : AnalyticsTransformer<ProductResponseDto> {
    override fun transform(input: ProductResponseDto, store: AnalyticsStore): DynamicContext {
        val originalPrice = input.price ?: 0.0
        
        // Örnek modifikasyon: 1000'den büyükse premium etiketle, değilse standart
        val category = if (originalPrice > 1000) "Premium" else "Standard"
        
        // Başka bir yerden gelmiş olabilecek 'currency' bilgisini store'dan okuma simülasyonu
        val currency = "TRY" 

        return DynamicContext(
            schemaName = "product_data",
            data = mapOf(
                "p_name" to (input.name ?: "Unknown"),
                "p_price" to originalPrice,
                "p_category" to category,
                "p_currency" to currency,
                "p_is_expensive" to (originalPrice > 500.0)
            )
        )
    }
}
