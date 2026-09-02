package com.example.core.analytics.manager

import com.example.core.analytics.store.AnalyticsStore
import com.example.core.analytics.transformer.AnalyticsTransformer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager responsible for ingesting raw data and updating the AnalyticsStore.
 */
@Singleton
class AnalyticsDataManager @Inject constructor(
    private val store: AnalyticsStore
) {
    /**
     * Ingests data using a specific transformer and updates the store.
     * @param data The raw data (e.g., DTO, Map, JSON string)
     * @param transformer The strategy to convert data into an AnalyticsContext
     * @param key The unique key to identify this data in the store (Use AnalyticsKeys)
     */
    fun <T> ingest(data: T, transformer: AnalyticsTransformer<T>, key: String) {
        val context = transformer.transform(data, store)
        store.updateContext(key, context)
    }

    /**
     * Helper to ingest raw JSON strings directly.
     */
    fun ingestJson(json: String, schemaName: String, key: String) {
        val transformer = com.example.core.analytics.transformer.JsonAnalyticsTransformer(schemaName)
        val context = transformer.transform(json, store)
        store.updateContext(key, context)
    }

    fun getStore(): AnalyticsStore = store
}
