package com.example.core.data.mapper

import com.example.core.domain.model.Product
import com.example.core.network.model.ProductResponseDto

fun ProductResponseDto.toDomain(): Product {
    return Product(
        id = this.id.orEmpty(),
        name = this.name.orEmpty(),
        price = this.price ?: 0.0,
        rating = this.rating ?: 0f,
        reviewCount = this.reviewCount ?: 0,
        description = this.description.orEmpty(),
        imageUrl = this.imageUrl
    )
}
