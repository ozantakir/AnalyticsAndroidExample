package com.example.core.data.repository

import com.example.core.data.mapper.toDomain
import com.example.core.domain.model.Product
import com.example.core.domain.repository.ProductRepository
import com.example.core.network.api.ProductApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {
    override fun getProduct(id: String): Flow<Product> = flow {
        val response = api.getProduct(id)
        emit(response.toDomain())
    }
}
