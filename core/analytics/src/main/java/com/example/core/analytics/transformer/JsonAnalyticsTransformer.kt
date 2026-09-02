package com.example.core.analytics.transformer

import com.example.core.analytics.context.AnalyticsContext
import com.example.core.analytics.context.DynamicContext
import com.example.core.analytics.store.AnalyticsStore
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.longOrNull

/**
 * A generic transformer that converts a JSON string into an AnalyticsContext.
 * Useful for late-binding raw service responses.
 */
class JsonAnalyticsTransformer(private val schemaName: String) : AnalyticsTransformer<String> {

    override fun transform(input: String, store: AnalyticsStore): AnalyticsContext {
        val jsonElement = Json.parseToJsonElement(input)
        val data = if (jsonElement is JsonObject) {
            jsonElement.toMap()
        } else {
            emptyMap()
        }
        
        return DynamicContext(
            schemaName = schemaName,
            data = data
        )
    }

    private fun JsonObject.toMap(): Map<String, Any?> {
        return this.mapValues { (_, value) -> value.toPrimitive() }
    }

    private fun JsonElement.toPrimitive(): Any? {
        return when (this) {
            is JsonNull -> null
            is JsonPrimitive -> {
                if (this.isString) {
                    this.content
                } else {
                    this.booleanOrNull ?: this.longOrNull ?: this.doubleOrNull ?: this.content
                }
            }
            is JsonObject -> this.toMap()
            is JsonArray -> this.map { it.toPrimitive() }
            else -> null
        }
    }
}
