package com.example.feature.product.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.analytics.AnalyticsKeys
import com.example.core.analytics.AnalyticsTracker
import com.example.core.analytics.generated.AddToCartClickedEvent
import com.example.core.analytics.generated.ProductViewEvent
import com.example.core.analytics.manager.AnalyticsDataManager
import com.example.core.analytics.transformer.ProductDomainTransformer
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
    private val getProductUseCase: GetProductUseCase,
    private val analyticsTracker: AnalyticsTracker,
    private val analyticsDataManager: AnalyticsDataManager
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
                    
                    // 1. Veriyi "product_details" referans anahtarıyla store'a kaydediyoruz.
                    // Transformer içindeki modifikasyonlar (segment, sale durumu vb.) burada yapılır.
                    analyticsDataManager.ingest(product, ProductDomainTransformer(), AnalyticsKeys.PRODUCT_DETAILS)

                    // 2. Track event
                    trackProductView(product)
                }
        }
    }

    private fun trackProductView(product: Product) {
        analyticsTracker.track(
            ProductViewEvent(
                productId = product.id
            )
        )
    }

    fun onAddToCartClicked(product: Product) {
        // Artik 'price' gibi değerleri elle geçmiyoruz.
        // AddToCartClickedEvent içindeki 'contextKey = "product_details"' sayesinde 
        // bu değerler Store'dan otomatik olarak (late-binding) çekilecek.
        analyticsTracker.track(
            AddToCartClickedEvent(
                productId = product.id,
                quantity = 1
            )
        )
    }
}
