### Skill: /generate-analytics-binding

**Goal:** Safely integrate analytics tracking calls into ViewModels, UseCases, or Jetpack Compose UI components using `:core:analytics` module events and `AnalyticsTracker`, supporting multiple destinations (Firebase, Adjust, Insider, etc.), base parameter fallbacks, target-specific context enrichment, and native SDK action dispatching.

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
   - **Base Parameter Map (`parameters`):** Store raw schema key-value pairs in `override val parameters: Map<String, Any?>`. Use this as the base payload for internal logging (Logcat, Crashlytics) or for SDKs that accept raw schema parameters without renaming.
   - **SDK-Specific Parameter Mapping:** Define explicit `when` branches inside `getMappedParameters()` only for SDKs requiring rigid key conventions (e.g., Firebase `item_id`, Adjust `revenue`).
   - **Fallback Mechanism:** `getMappedParameters()` must always end with `else -> parameters` so SDKs without custom transformation rules automatically receive the base parameter map.
   - **Native Action Keys:** If a target requires a strongly-typed native method call (e.g., Insider's `cart.add()`, Facebook's `logPurchase()`), pass internal action flags (e.g., `"__action_type" to "CART_ADD"`) so `AnalyticsTracker` can invoke the native SDK method.
4. **Global & Target-Specific Context Isolation:**
   - **Global & Dynamic Parameters:** Never pass common or runtime-changing parameters (e.g., `user_id` on login, `locale` on language switch, `app_version`) into individual `EventModel` constructors or UI callers.
   - **Dynamic Provider Injection:** Inject or update these parameters at the `AnalyticsTracker` layer using `AnalyticsTracker.setProviders(...)` or `AnalyticsTracker.updateGlobalParameters(...)` whenever state changes (e.g., Auth state, App launch, Settings update). They are dynamically resolved at dispatch time.
5. **Thread Safety:** Analytics tracking calls must be non-blocking and safe to call on any thread or coroutine context.

---

### Code & Template Structures

#### 1. Multi-Target Event Class Structure (`:core:analytics`)

```kotlin
package com.example.core.analytics.generated

import com.example.core.analytics.AnalyticsDestination
import com.example.core.analytics.EventModel

data class AddToCartClickedEvent(
    val productId: String,
    val price: Double,
    val quantity: Int,
    val category: String? = null
) : EventModel() {

    override val eventName: String = "add_to_cart_clicked"

    // Multi-Destination Declaration
    override val destinations: List<AnalyticsDestination> = listOf(
        AnalyticsDestination.FIREBASE,
        AnalyticsDestination.ADJUST,
        AnalyticsDestination.INSIDER
    )

    // Base Raw Schema Parameters (Used for internal logging or SDKs without custom key mapping)
    override val parameters: Map<String, Any?> = mapOf(
        "productId" to productId,
        "price" to price,
        "quantity" to quantity,
        "category" to category
    )

    override fun getMappedParameters(destination: AnalyticsDestination): Map<String, Any?> {
        return when (destination) {
            // SDKs requiring custom key transformations
            AnalyticsDestination.FIREBASE -> mapOf(
                "item_id" to productId,
                "value" to price,
                "quantity" to quantity,
                "item_category" to category
            )
            AnalyticsDestination.ADJUST -> mapOf(
                "revenue" to price
            )
            AnalyticsDestination.INSIDER -> mapOf(
                // Action flag for native SDK method execution
                "__action_type" to "CART_ADD",
                "product_id" to productId,
                "price" to price,
                "quantity" to quantity
            )
            // SDKs accepting raw schema keys fall back to standard parameters
            else -> parameters
        }
    }
}