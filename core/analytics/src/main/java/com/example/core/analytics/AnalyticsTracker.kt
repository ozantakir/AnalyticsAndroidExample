package com.example.core.analytics

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsTracker {
    fun track(event: EventModel)
    
    /**
     * Birden fazla dinamik veri sağlayıcıyı (Auth state, Config vb.) enjekte etmek için kullanılır.
     */
    fun setProviders(vararg providers: () -> Map<String, Any?>)

    /**
     * Mevcut global parametreleri çalışma zamanında güncellemek/eklemek için kullanılır.
     */
    fun updateGlobalParameters(params: Map<String, Any?>)
}

@Singleton
class AnalyticsTrackerImpl @Inject constructor(
    @ApplicationContext private val context: Context
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
        // 1. Tüm global verileri birleştir (Statik Map + Dinamik Provider'lar)
        val combinedGlobalParams = mutableMapOf<String, Any?>().apply {
            putAll(globalParameters)
            providers.forEach { provider -> putAll(provider()) }
        }

        val targets = if (event.destinations.contains(AnalyticsDestination.ALL)) {
            AnalyticsDestination.entries.filter { it != AnalyticsDestination.ALL }
        } else {
            event.destinations
        }

        targets.forEach { destination ->
            val eventParams = event.getMappedParameters(destination)
            val contextParams = resolveContextParameters(destination)

            // 2. Hiyerarşik Birleştirme: Global + Event + Context
            val finalParams = combinedGlobalParams + eventParams + contextParams

            if (finalParams.containsKey("__action_type")) {
                dispatchNativeAction(destination, event.eventName, finalParams)
            } else {
                logToGenericSdk(destination, event.eventName, finalParams)
            }
        }
    }

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
        Log.d("AnalyticsTracker", "[$destination] Event: $eventName | Params: $params")
    }

    private fun dispatchNativeAction(destination: AnalyticsDestination, eventName: String, params: Map<String, Any?>) {
        val actionType = params["__action_type"]
        Log.d("AnalyticsTracker", "[$destination] Native Action ($actionType): $eventName | Params: $params")
    }
}
