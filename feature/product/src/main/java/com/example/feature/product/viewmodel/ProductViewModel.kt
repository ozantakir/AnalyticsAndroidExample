package com.example.feature.product.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.analytics.AnalyticsTracker
import com.example.core.analytics.generated.AddToCartClickedEvent
import com.example.core.analytics.generated.ProductViewEvent
import com.example.core.domain.model.Product
import com.example.core.domain.usecase.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProductUiState {
    data object Loading : ProductUiState
    data class Success(val product: Product) : ProductUiState
    data class Error(val message: String) : ProductUiState
}

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductUiState>(ProductUiState.Loading)
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            getProductUseCase(productId)
                .onStart { _uiState.value = ProductUiState.Loading }
                .catch { e -> _uiState.value = ProductUiState.Error(e.message ?: "Unknown Error") }
                .collect { product ->
                    _uiState.value = ProductUiState.Success(product)
                    trackProductView(product)
                }
        }
    }

    private fun trackProductView(product: Product) {
        AnalyticsTracker.track(
            ProductViewEvent(
                productId = product.id
            )
        )
    }

    fun onAddToCartClicked(product: Product) {
        // Handle add to cart logic
        AnalyticsTracker.track(
            AddToCartClickedEvent(
                productId = product.id,
                price = product.price,
                quantity = 1
            )
        )
    }
}