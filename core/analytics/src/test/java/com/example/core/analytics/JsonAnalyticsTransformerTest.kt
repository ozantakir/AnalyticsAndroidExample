package com.example.core.analytics

import com.example.core.analytics.store.AnalyticsStore
import com.example.core.analytics.transformer.JsonAnalyticsTransformer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class JsonAnalyticsTransformerTest {

    private lateinit var transformer: JsonAnalyticsTransformer
    private lateinit var store: AnalyticsStore

    @Before
    fun setUp() {
        transformer = JsonAnalyticsTransformer("test_json_schema")
        store = AnalyticsStore()
    }

    @Test
    fun `transform should parse flat JSON correctly`() {
        val json = """
            {
                "id": "123",
                "price": 99.99,
                "is_active": true,
                "stock": 10,
                "null_val": null
            }
        """.trimIndent()

        val context = transformer.transform(json, store)
        val data = context.toMap()

        assertEquals("123", data["id"])
        assertEquals(99.99, data["price"])
        assertEquals(true, data["is_active"])
        assertEquals(10L, data["stock"])
        assertNull(data["null_val"])
    }

    @Test
    fun `transform should parse nested JSON objects correctly`() {
        val json = """
            {
                "user": {
                    "name": "John",
                    "details": {
                        "age": 30
                    }
                }
            }
        """.trimIndent()

        val context = transformer.transform(json, store)
        val data = context.toMap()

        val user = data["user"] as Map<*, *>
        assertEquals("John", user["name"])
        
        val details = user["details"] as Map<*, *>
        assertEquals(30L, details["age"])
    }

    @Test
    fun `transform should parse JSON arrays correctly`() {
        val json = """
            {
                "tags": ["electronics", "sale"],
                "scores": [1, 2, 3]
            }
        """.trimIndent()

        val context = transformer.transform(json, store)
        val data = context.toMap()

        assertEquals(listOf("electronics", "sale"), data["tags"])
        assertEquals(listOf(1L, 2L, 3L), data["scores"])
    }

    @Test
    fun `transform should handle empty JSON object`() {
        val json = "{}"
        val context = transformer.transform(json, store)
        val data = context.toMap()
        assertEquals(0, data.size)
    }
}
