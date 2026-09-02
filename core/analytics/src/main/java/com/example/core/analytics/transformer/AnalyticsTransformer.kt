package com.example.core.analytics.transformer

import com.example.core.analytics.context.AnalyticsContext
import com.example.core.analytics.store.AnalyticsStore

/**
 * Interface for transforming raw data (JSON, DTO, etc.) into an AnalyticsContext.
 */
interface AnalyticsTransformer<T> {
    fun transform(input: T, store: AnalyticsStore): AnalyticsContext
}
