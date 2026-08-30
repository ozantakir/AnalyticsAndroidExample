package com.example.core.network.api

import com.example.core.network.model.ProductResponseDto
import kotlinx.coroutines.delay

class ProductApi {
    // Mocking an API call
    suspend fun getProduct(id: String): ProductResponseDto {
        delay(1000) // Simulate network delay
        return ProductResponseDto(
            id = id,
            name = "Wireless Noise-Canceling Headphones",
            price = 299.99,
            rating = 4.8f,
            reviewCount = 2341,
            description = "High-quality wireless headphones with active noise cancellation.",
            imageUrl = null
        )
    }
}
