package com.example.core.analytics

/**
 * Centralized keys for analytics data storage and reference.
 * Use these constants in AnalyticsDataManager.ingest and EventModel.contextKey
 * to ensure late-binding data matches correctly.
 */
object AnalyticsKeys {
    const val PRODUCT_DETAILS = "product_details"
    const val USER_CONTEXT = "user_context"
    const val CART_CONTEXT = "cart_context"
}
