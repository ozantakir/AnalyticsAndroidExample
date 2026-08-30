# Project Architectural Rules & Guidelines

## Architecture & Tech Stack
- **Architecture**: MVVM + Clean Architecture (Data, Domain, Presentation)
- **UI**: Jetpack Compose (100% Kotlin)
- **DI**: Hilt
- **Async & Reactive**: Coroutines + Flow
- **JSON Serialization**: KotlinX Serialization (`@Serializable`)

---

## Modularization & Feature Structure

### Module Layering
- **`:app`**: Aggregates feature modules and configures the top-level navigation and DI graph.
- **`:feature:{name}`**: Contains presentation logic (Compose UI, ViewModels, Feature Navigation, Local UiState). Depends on `:core:analytics` and `:core:domain`.
- **`:core:analytics`**: Houses `AnalyticsTracker`, `EventModel`, destination mappings, and auto-generated analytics event data classes from the schema pipeline.
- **`:core:domain`**: Holds UseCases, Domain Models, and Repository Interfaces (Clean Architecture abstractions). Free of Android dependencies.
- **`:core:data`**: Implements Repository interfaces, orchestrates local databases (`:core:database`), network services (`:core:network`), and data mappers.
- **`:core:network`**: Handles Retrofit/Ktor APIs, DTOs, network interceptors, and response wrappers.
- **`:core:designsystem`**: Reusable Compose UI components, tokens, typography, and themes.

### Feature & Cross-Module Rules
- **Feature Isolation:** Every new UI feature must reside in its own feature module (e.g., `:feature:cart`). Feature modules must NEVER depend directly on each other.
- **Analytics Rule:** Feature modules must NEVER implement native SDK calls directly. All tracking must pass through `:core:analytics` via `AnalyticsTracker.track(event)`.

---

## General Coding & Clean Architecture Standards
- Always prefer `val` over `var` and immutable collections.
- Use `sealed interface` for UI state management: `sealed interface ScreenUiState { data object Loading : ScreenUiState ... }`
- ViewModels must NOT expose `MutableStateFlow` directly; use read-only `StateFlow` via `.asStateFlow()`.
- Avoid passing raw ViewModels to child Composables; pass state data and lambda event callbacks instead (Unidirectional Data Flow).
- The Domain layer must remain pure Kotlin and completely free of any `android.*` imports.

---

## Network Request & Response Rules

### DTO Structure & Location
- Place all API models inside `:core:network` under `com.example.project.core.network.model`.
- Append explicit suffixes: Use `RequestDto` for request bodies (e.g., `AddToCartRequestDto`) and `ResponseDto` for response payloads (e.g., `CartResponseDto`).

### DTO Isolation & Domain Mapping
- **No Leaks:** Never use `RequestDto` or `ResponseDto` inside `:core:domain`, `:core:analytics`, or `:feature` modules.
- **Mappers:** Every `ResponseDto` must provide an extension function or mapper class inside `:core:data` to convert itself into a pure Domain Model:
  ```kotlin
  // Located inside :core:data
  fun CartResponseDto.toDomain(): CartModel {
      return CartModel(
          id = this.cartId.orEmpty(),
          totalPrice = this.totalAmount ?: 0.0,
          items = this.items?.map { it.toDomain() } ?: emptyList()
      )
  }

## Analytics Target & Destination Rules
- **Target Abstraction:** Every event model must explicitly define its destination targets using the `AnalyticsDestination` enum (e.g., `FIREBASE`, `ADJUST`, `INSIDER`, `FACEBOOK`).
- **Dynamic Parameter Mapping:** `EventModel` classes must handle custom payload transformations per destination inside `getMappedParameters(destination: AnalyticsDestination)`.
- **Native Method Dispatching:** If a target requires a native SDK method call (e.g., Insider's `cart.add()`, Facebook's `logPurchase()`) instead of a generic key-value log, `AnalyticsTracker` must evaluate action keys (e.g., `__action_type`) and dispatch to the corresponding native SDK method.

## Analytics Model & Parameter Mapping Standards
- **Raw Base Parameters (`parameters`):** Every `EventModel` maintains a base `parameters` map containing the exact schema properties. This serves as the raw, unmapped payload (ideal for internal debugging, Logcat, and Crashlytics logs).
- **Destination-Specific Mapping:** Explicit mapping branches inside `getMappedParameters(destination)` are used ONLY for SDKs that require specific parameter names or structures (e.g., Firebase requiring `item_id` instead of `productId`, or Adjust requiring `revenue`).
- **Fallback Rule:** `getMappedParameters()` must always end with `else -> parameters`. SDKs that accept standard schema keys without custom transformations will automatically consume this base map without extra code overhead.

## Global & Common Analytics Parameters
- **Global Parameters Isolation:** Common/global parameters (e.g., `device_id`, `language`) must NEVER be declared inside individual JSON schemas or `EventModel` classes.
- **Tracker Level Injection:** Global parameters are injected centrally at startup via `AnalyticsTracker.setGlobalParameterProvider { ... }` and merged automatically with event-specific payloads during `AnalyticsTracker.track()`.
- **Target-Specific Context Parameters:** Destination-specific environment variables (e.g., `device_id` for Firebase, `locale` for Insider, `network_type` for Custom Collectors) must NOT be declared inside event schemas or UI callers. They must be resolved dynamically at the `AnalyticsTracker` layer during dispatching based on the active `AnalyticsDestination`.