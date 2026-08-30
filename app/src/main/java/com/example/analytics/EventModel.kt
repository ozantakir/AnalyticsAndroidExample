package com.example.analytics

abstract class EventModel(
    val eventName: String,
    val parameters: Map<String, Any?>,
    val destinations: List<AnalyticsDestination>
) {
    abstract fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?>
}
