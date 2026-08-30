package com.example.core.analytics

/**
 * Base model for all analytics events.
 * Follows GEMINI.md multi-destination and parameter mapping rules.
 */
abstract class EventModel {
    abstract val eventName: String
    
    // Base Raw Schema Parameters (Used for internal logging or SDKs without custom key mapping)
    abstract val parameters: Map<String, Any?>

    // Default to ALL destinations if not specified
    open val destinations: List<AnalyticsDestination> = listOf(AnalyticsDestination.ALL)

    /**
     * Transforms the event payload for a specific analytics provider.
     * Use "__action_type" key for native SDK method dispatching.
     */
    abstract fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?>
}
