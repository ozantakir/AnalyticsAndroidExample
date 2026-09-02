### Skill: /generate-analytics-binding

**Goal:** Safely integrate analytics tracking calls into ViewModels, UseCases, or Jetpack Compose UI components using `:core:analytics` module events and `AnalyticsTracker`. Supports late-binding via `AnalyticsStore`, interface-based SDK mapping, and type-safe key management.

---

### Integration & Architectural Rules

1. **Single Entry Point:**
   - Always route tracking calls through `AnalyticsTracker.track(event)`.
   - Feature modules must NEVER import or invoke underlying analytics SDKs directly.

2. **Where to Trigger:**
   - **User Actions:** Trigger from ViewModel functions or Compose event lambdas.
   - **Screen Impressions:** Use `LaunchedEffect(Unit)` in Compose.
   - **State-Driven Events:** Trigger during ViewModel state transitions (Success/Failure).

3. **Interface-Based SDK Mapping (Crucial):**
   - Events should NOT use a monolithic mapping method.
   - Implement SDK-specific interfaces from `:core:analytics:destination`:
     - `FirebaseEvent` -> `toFirebaseParams(referenceData: Map<String, Any?>)`
     - `InsiderEvent` -> `toInsiderParams(referenceData: Map<String, Any?>)`
     - `AdjustEvent` -> `toAdjustParams(referenceData: Map<String, Any?>)`
   - Always use the `referenceData` passed to these methods to access late-bound parameters.

4. **Late-Binding & AnalyticsStore:**
   - Use `contextKeys: List<String>` in `EventModel` to request data from `AnalyticsStore`.
   - Centralize all keys in `AnalyticsKeys` object (e.g., `AnalyticsKeys.PRODUCT_DETAILS`).
   - Use `AnalyticsDataManager.ingest()` or `ingestJson()` in ViewModels to store data before tracking.

5. **Hierarchical Merging Logic:**
   - `AnalyticsTracker` automatically merges:
     1. Global Parameters (Auth state, etc.)
     2. Reference Data (from Store via `contextKeys`)
     3. Event Parameters (from SDK interfaces)
     4. Target-specific Context (OS version, Locale, etc.)

---

### Code & Template Structures

#### 1. Multi-Target Event Class Structure (`:core:analytics`)

```kotlin
package com.example.core.analytics.generated

import com.example.core.analytics.AnalyticsDestination
import com.example.core.analytics.EventModel
import com.example.core.analytics.AnalyticsKeys
import com.example.core.analytics.destination.*

data class AddToCartClickedEvent(
    val quantity: Int
) : EventModel(), FirebaseEvent, InsiderEvent {

    override val eventName: String = "add_to_cart_clicked"

    // Request late-binding data from Store
    override val contextKeys: List<String> = listOf(AnalyticsKeys.PRODUCT_DETAILS)

    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE,
        AnalyticsDestination.INSIDER
    )

    override val parameters: Map<String, Any?> = mapOf("quantity" to quantity)

    override fun toFirebaseParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        return mapOf(
            "item_id" to referenceData["p_id"],
            "value" to referenceData["p_price"],
            "quantity" to quantity
        )
    }

    override fun toInsiderParams(referenceData: Map<String, Any?>): Map<String, Any?> {
        return mapOf(
            "__action_type" to "CART_ADD",
            "product_name" to referenceData["p_name"]
        )
    }
}
```

#### 2. Data Ingestion in ViewModel

```kotlin
// Ingesting data before tracking
analyticsDataManager.ingest(
    data = product,
    transformer = ProductDomainTransformer(),
    key = AnalyticsKeys.PRODUCT_DETAILS
)

// Tracking the event
analyticsTracker.track(AddToCartClickedEvent(quantity = 1))
```
