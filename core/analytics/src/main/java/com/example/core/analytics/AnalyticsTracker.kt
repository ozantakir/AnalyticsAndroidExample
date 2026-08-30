package com.example.core.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsTracker {
    fun track(event: EventModel)
}

@Singleton
class AnalyticsTrackerImpl @Inject constructor() : AnalyticsTracker {
    
    override fun track(event: EventModel) {
        val targets = if (event.destinations.contains(AnalyticsDestination.ALL)) {
            AnalyticsDestination.entries.filter { it != AnalyticsDestination.ALL }
        } else {
            event.destinations
        }

        targets.forEach { destination ->
            val params = event.getMappedParameters(destination)
            if (params.containsKey("__action_type")) {
                dispatchNativeAction(destination, event.eventName, params)
            } else {
                logToGenericSdk(destination, event.eventName, params)
            }
        }
    }

    private fun logToGenericSdk(
        destination: AnalyticsDestination,
        eventName: String,
        params: Map<String, Any?>
    ) {
        // In real implementation, call specific SDKs: FirebaseAnalytics.getInstance().logEvent(...)
        Log.d("AnalyticsTracker", "[$destination] Generic Event: $eventName | Params: $params")
    }

    private fun dispatchNativeAction(
        destination: AnalyticsDestination,
        eventName: String,
        params: Map<String, Any?>
    ) {
        val actionType = params["__action_type"]
        // In real implementation, call specific native methods: Insider.Instance.itemAddedToCart(...)
        Log.d("AnalyticsTracker", "[$destination] Native Action ($actionType): $eventName | Params: $params")
    }
}
