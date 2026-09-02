package com.example.core.analytics.context

/**
 * Base interface for all analytics data sets.
 */
interface AnalyticsContext {
    fun toMap(): Map<String, Any?>
}

/**
 * A generic context for simple key-value pairs or dynamic data.
 */
data class DynamicContext(
    val schemaName: String,
    val data: Map<String, Any?>
) : AnalyticsContext {
    override fun toMap() = data
}
