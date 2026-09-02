package com.example.core.analytics

import android.content.Context
import android.util.Log
import com.example.core.analytics.store.AnalyticsStore
import com.example.core.analytics.destination.AdjustEvent
import com.example.core.analytics.destination.FirebaseEvent
import com.example.core.analytics.destination.InsiderEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsTracker {
    fun track(event: EventModel)
    
    /**
     * Injects multiple dynamic data providers (Auth state, Config, etc.).
     * Follows Skill Rule 4 from generate-analytics-binding.md.
     */
    fun setProviders(vararg providers: () -> Map<String, Any?>)

    /**
     * Updates/adds to the current global parameters at runtime.
     * Useful for setting user_id after login.
     */
    fun updateGlobalParameters(params: Map<String, Any?>)
}

@Singleton
class AnalyticsTrackerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analyticsStore: AnalyticsStore
) : AnalyticsTracker {

    private val providers = mutableListOf<() -> Map<String, Any?>>()
    private val globalParameters = mutableMapOf<String, Any?>()

    override fun setProviders(vararg providers: () -> Map<String, Any?>) {
        this.providers.clear()
        this.providers.addAll(providers)
    }

    override fun updateGlobalParameters(params: Map<String, Any?>) {
        this.globalParameters.putAll(params)
    }

    override fun track(event: EventModel) {
        // 1. Merge all global data (Static Map + Dynamic Providers)
        val combinedGlobalParams = mutableMapOf<String, Any?>().apply {
            putAll(globalParameters)
            providers.forEach { provider -> putAll(provider()) }
        }

        // 2. Resolve Late-Binding Context Data
        // Fallback: If no specific keys provided, use all available data from store
        val referenceData = if (event.contextKeys.isEmpty()) {
            analyticsStore.getAllData()
        } else {
            val data = mutableMapOf<String, Any?>()
            event.contextKeys.forEach { key ->
                analyticsStore.getContext(key)?.toMap()?.let { data.putAll(it) }
            }
            data
        }

        val targets = if (event.destinations.contains(AnalyticsDestination.ALL)) {
            AnalyticsDestination.entries.filter { it != AnalyticsDestination.ALL }
        } else {
            event.destinations
        }

        targets.forEach { destination ->
            val eventParams = when (destination) {
                AnalyticsDestination.FIREBASE -> (event as? FirebaseEvent)?.toFirebaseParams(referenceData)
                AnalyticsDestination.INSIDER -> (event as? InsiderEvent)?.toInsiderParams(referenceData)
                AnalyticsDestination.ADJUST -> (event as? AdjustEvent)?.toAdjustParams(referenceData)
                else -> event.parameters
            } ?: event.parameters

            val contextParams = resolveContextParameters(destination)

            // 3. Hierarchical Merge: Global + Reference (Store) + Event + Context (Target-specific)
            val finalParams = combinedGlobalParams + referenceData + eventParams + contextParams

            if (finalParams.containsKey("__action_type")) {
                dispatchNativeAction(destination, event.eventName, finalParams)
            } else {
                logToGenericSdk(destination, event.eventName, finalParams)
            }
        }
    }

    /**
     * Resolves target-specific dynamic values according to GEMINI.md.
     * These values are NOT defined inside the EventModel.
     */
    private fun resolveContextParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
            AnalyticsDestination.FIREBASE -> mapOf(
                "os_version" to android.os.Build.VERSION.RELEASE,
                "device_model" to android.os.Build.MODEL
            )
            AnalyticsDestination.INSIDER -> mapOf(
                "locale" to context.resources.configuration.locales[0].toLanguageTag()
            )
            else -> emptyMap()
        }
    }

    private fun logToGenericSdk(destination: AnalyticsDestination, eventName: String, params: Map<String, Any?>) {
        // In real implementation, call specific SDKs: FirebaseAnalytics.getInstance().logEvent(...)
        Log.d("AnalyticsTracker", "[$destination] Event: $eventName | Params: $params")
    }

    private fun dispatchNativeAction(destination: AnalyticsDestination, eventName: String, params: Map<String, Any?>) {
        val actionType = params["__action_type"]
        // In real implementation, call specific native methods: Insider.Instance.itemAddedToCart(...)
        Log.d("AnalyticsTracker", "[$destination] Native Action ($actionType): $eventName | Params: $params")
    }
}
