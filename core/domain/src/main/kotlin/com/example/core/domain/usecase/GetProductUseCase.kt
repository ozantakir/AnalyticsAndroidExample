package com.example.core.domain.usecase

import com.example.core.domain.model.Product
import com.example.core.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(
    private val repository: ProductRepository
) {
    operator fun invoke(productId: String): Flow<Product> {
        return repository.getProduct(productId)
    }
}
