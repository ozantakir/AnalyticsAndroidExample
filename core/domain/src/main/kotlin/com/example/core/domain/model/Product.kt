package com.example.core.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val rating: Float,
    val reviewCount: Int,
    val description: String,
    val imageUrl: String? = null
)
