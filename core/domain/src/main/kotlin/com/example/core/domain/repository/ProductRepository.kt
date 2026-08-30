package com.example.core.domain.repository

import com.example.core.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProduct(id: String): Flow<Product>
}
