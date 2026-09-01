# Analytics Core Integration & AI Binding Agent

This repository contains the production application code and the target `:core:analytics` module.

## Architecture Rules
- **Single Entry Point:** All event tracking must route strictly through `AnalyticsTracker.track(event)`. Direct invocation of third-party SDKs (Firebase, Adjust, Insider) is strictly prohibited.
- **Base Payload Fallback:** All generated events inherit a base `parameters` map. Custom key renaming inside `getMappedParameters()` is reserved exclusively for SDKs with rigid key requirements (e.g., Firebase `item_id`), defaulting to `else -> parameters`.
- **Dynamic Context Isolation:** Global and environment-specific parameters (`user_id`, `locale`, `device_id`, `app_version`) are injected dynamically at the `AnalyticsTracker` layer and must **never** be passed into individual event constructors.

## AI Automated Event Binding
This repository uses an automated CI/CD AI Agent step (`.github/workflows/auto-analytics-binding.yml`):
1. Newly generated events arriving via Schema PRs are analyzed.
2. The AI Agent scans ViewModels, UseCases, and Compose UI files.
3. `AnalyticsTracker.track(...)` calls are automatically injected into the appropriate trigger points for reviewer approval.
