package com.example.core.analytics

/**
 * Base model for all analytics events.
 * Only contains identity and data reference keys.
 */
abstract class EventModel {
    abstract val eventName: String
    
    /**
     * Base Raw Schema Parameters (fallback).
     */
    abstract val parameters: Map<String, Any?>

    /**
     * Keys to pull data from AnalyticsStore.
     */
    open val contextKeys: List<String> = emptyList()

    /**
     * Optional explicit destinations.
     */
    open val destinations: List<AnalyticsDestination> = listOf(AnalyticsDestination.ALL)
}
