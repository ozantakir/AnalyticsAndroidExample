package com.example.core.analytics

import com.example.core.analytics.store.AnalyticsStore
import com.example.core.analytics.transformer.ProductDomainTransformer
import com.example.core.domain.model.Product
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductDomainTransformerTest {

    private lateinit var transformer: ProductDomainTransformer
    private lateinit var store: AnalyticsStore

    @Before
    fun setUp() {
        transformer = ProductDomainTransformer()
        store = AnalyticsStore()
    }

    @Test
    fun `transform should map high price to High-Value segment`() {
        val product = Product(
            id = "1",
            name = "Laptop",
            price = 1500.0,
            rating = 4.5f,
            reviewCount = 100,
            description = "High end laptop"
        )

        val context = transformer.transform(product, store)
        val data = context.toMap()

        assertEquals("Laptop", data["p_name"])
        assertEquals(1500.0, data["p_price"])
        assertEquals("High-Value", data["p_segment"])
        assertEquals(false, data["p_is_sale"])
    }

    @Test
    fun `transform should map low price to Normal segment and marked as sale`() {
        val product = Product(
            id = "2",
            name = "Mouse",
            price = 50.0,
            rating = 4.0f,
            reviewCount = 50,
            description = "Cheap mouse"
        )

        val context = transformer.transform(product, store)
        val data = context.toMap()

        assertEquals("Mouse", data["p_name"])
        assertEquals(50.0, data["p_price"])
        assertEquals("Normal", data["p_segment"])
        assertEquals(true, data["p_is_sale"])
    }
}
