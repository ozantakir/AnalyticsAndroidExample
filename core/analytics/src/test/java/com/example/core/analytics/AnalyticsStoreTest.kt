package com.example.core.analytics

import com.example.core.analytics.context.AnalyticsContext
import com.example.core.analytics.store.AnalyticsStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class AnalyticsStoreTest {

    private lateinit var store: AnalyticsStore

    @Before
    fun setUp() {
        store = AnalyticsStore()
    }

    @Test
    fun `updateContext should store context and getContext should retrieve it`() {
        val key = "testKey"
        val context = object : AnalyticsContext {
            override fun toMap(): Map<String, Any?> = mapOf("param1" to "value1")
        }

        store.updateContext(key, context)

        val retrieved = store.getContext(key)
        assertEquals(context, retrieved)
        assertEquals(mapOf("param1" to "value1"), retrieved?.toMap())
    }

    @Test
    fun `getContext should return null if key does not exist`() {
        assertNull(store.getContext("nonExistent"))
    }

    @Test
    fun `getAllData should aggregate all context data`() {
        val context1 = object : AnalyticsContext {
            override fun toMap(): Map<String, Any?> = mapOf("a" to 1)
        }
        val context2 = object : AnalyticsContext {
            override fun toMap(): Map<String, Any?> = mapOf("b" to 2)
        }

        store.updateContext("key1", context1)
        store.updateContext("key2", context2)

        val allData = store.getAllData()
        assertEquals(mapOf("a" to 1, "b" to 2), allData)
    }

    @Test
    fun `clear should remove all contexts`() {
        val context = object : AnalyticsContext {
            override fun toMap(): Map<String, Any?> = mapOf("a" to 1)
        }
        store.updateContext("key", context)
        
        store.clear()
        
        assertNull(store.getContext("key"))
        assertEquals(0, store.getAllData().size)
    }
}
