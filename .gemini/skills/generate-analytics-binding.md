### Skill: /generate-analytics-binding

**Goal:** Safely integrate analytics tracking calls into ViewModels, UseCases, or Jetpack Compose UI components using `:core:analytics` module events and `AnalyticsTracker`, supporting multiple destinations (Firebase, Adjust, Insider, etc.) and native SDK action dispatching.

---

### Integration & Architectural Rules

1. **Single Entry Point:**
    - Always route tracking calls through `AnalyticsTracker.track(event)`.
    - Feature modules and UI layers must NEVER import or invoke underlying analytics SDKs directly.
2. **Where to Trigger:**
    - **User Actions (Click, Scroll, Toggle):** Trigger from ViewModel functions handling user intents OR directly inside Compose event lambdas (e.g., `onClick = { ... }`).
    - **Screen Impression / Page View:** Trigger inside a Compose `LaunchedEffect(Unit)` block when the screen first renders.
    - **State-Driven Events (Success/Failure):** Trigger inside ViewModel coroutine flows when a state transition succeeds or fails.
3. **Multi-Destination & Action Handling:**
    - **Target Routing:** Events must explicitly list their targets using `AnalyticsDestination` (e.g., `FIREBASE`, `ADJUST`, `INSIDER`, `FACEBOOK`).
    - **Custom Parameter Mapping:** `EventModel` classes must handle payload transformations per target inside `getMappedParameters(destination: AnalyticsDestination)`.
    - **Native Action Keys:** If a target requires a strongly-typed native method call (e.g., Insider's `cart.add()`, Facebook's `logPurchase()`), pass internal action flags (e.g., `"__action_type" to "CART_ADD"`) so `AnalyticsTracker` can invoke the native SDK method.
4. **Thread Safety:** Analytics tracking calls must be non-blocking and safe to call on any thread or coroutine context.

---

### Code & Template Structures

#### 1. Multi-Target Event Class Structure (`:core:analytics`)

```kotlin
package com.example.project.core.analytics.events

import com.example.project.core.analytics.model.AnalyticsDestination
import com.example.project.core.analytics.model.EventModel

data class AddToCartClickedEvent(
    val productId: String,
    val price: Double,
    val quantity: Int,
    val name: String
) : EventModel() {

    override val eventName: String = "add_to_cart_clicked"

    // Multi-Destination Declaration
    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE,
        AnalyticsDestination.ADJUST,
        AnalyticsDestination.INSIDER
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
            AnalyticsDestination.FIREBASE -> mapOf(
                "item_id" to productId,
                "value" to price,
                "quantity" to quantity
            )
            AnalyticsDestination.ADJUST -> mapOf(
                "event_token" to "cart_add_token_123",
                "revenue" to price
            )
            AnalyticsDestination.INSIDER -> mapOf(
                // Action flag for native SDK method execution
                "__action_type" to "CART_ADD",
                "product_id" to productId,
                "price" to price,
                "quantity" to quantity,
                "name" to name
            )
            else -> emptyMap()
        }
    }
}