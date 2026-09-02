package com.example.core.analytics.store

import com.example.core.analytics.context.AnalyticsContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central store for all analytics contexts.
 * Maintains the current state of data to be included in events.
 */
@Singleton
class AnalyticsStore @Inject constructor() {
    private val contexts = mutableMapOf<String, AnalyticsContext>()

    fun updateContext(key: String, context: AnalyticsContext) {
        synchronized(contexts) {
            contexts[key] = context
        }
    }

    fun getContext(key: String): AnalyticsContext? {
        return synchronized(contexts) {
            contexts[key]
        }
    }

    /**
     * Aggregates all context data into a single map.
     */
    fun getAllData(): Map<String, Any?> {
        return synchronized(contexts) {
            contexts.values.flatMap { it.toMap().toList() }.toMap()
        }
    }
    
    fun clear() {
        synchronized(contexts) {
            contexts.clear()
        }
    }
}
