package com.example.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    @SerialName("id") val id: String?,
    @SerialName("name") val name: String?,
    @SerialName("price") val price: Double?,
    @SerialName("rating") val rating: Float?,
    @SerialName("reviewCount") val reviewCount: Int?,
    @SerialName("description") val description: String?,
    @SerialName("imageUrl") val imageUrl: String?
)
