# Architecture migration checkpoint

The refactor is intentionally staged so every merge remains buildable. The
composition root is responsible for wiring Koin and Voyager; feature code uses
domain contracts; data code owns API DTO mapping, persistence, and cache policy.

## Implemented in this checkpoint

- `base:core` provides `Money`, `FixedDecimal`, `AppResult`, `AppError`, cache
  freshness, UI contracts, redacted logging, and performance tracing. `Money`
  uses a compatibility serializer so numeric values from pre-migration cache
  entries are upgraded without losing offline content.
- Active source packages follow the bounded-context names (`data.auth`,
  `data.portfolio`, `data.social`, `network`, and `security.*`). Historical
  `*_api`, `data_store`, and provider package names remain only in preserved
  uncompiled archives.
- `base:database` provides SQLDelight-backed dashboard, notification, and
  bounded-context feature snapshots for Android and iOS. The generic
  `FeatureCache` contract keeps repositories independent of SQLDelight, and
  schema migration `1.sqm` upgrades existing installs without deleting old
  snapshots.
- Session tokens migrate from legacy preferences to Android Keystore or iOS
  Keychain with write/read-back verification and retry-safe fallback. The
  former `functional:data-store` project is retired from the graph; its
  active session implementation now lives under `base:security`.
- Android backup and device-transfer rules exclude the secure-session
  preferences and the legacy token DataStore file, so credentials never leave
  the installation through Auto Backup.
- Ktor clients use bounded timeouts, idempotent-only retries, a separate public
  client for auth/recovery endpoints, an authenticated singleton for app data,
  a separate refresh request, and single-flight refresh coordination.
- A transient refresh outage keeps the local session intact; tokens are cleared
  only after an explicit refresh authentication rejection or a successful
  response with no usable token pair. Network exceptions and 5xx responses are
  treated as retryable failures rather than forced logout.
- Ktor engine selection is platform-specific: Android uses CIO and iOS uses
  Darwin behind an expect/actual boundary, so common networking code cannot
  accidentally instantiate the Android engine on iOS.
- The network layer now consumes the `domain:auth` `SessionTokenStore` contract;
  DataStore/Keystore details are bound only in the composition root.
- Authenticated user mutations reuse the Koin singleton HTTP client instead of
  creating a client per request; cache repositories use a shared `SingleFlight`
  gate so concurrent callers do not duplicate the same GET.
- The global qualified `Json` instance is registered once by the network
  composition root. Transport modules consume that binding instead of
  redefining the same Koin singleton, removing module-order-dependent decoding
  policy.
- All API modules now use the shared Ktor `HttpClient` directly. Ktorfit and its
  compiler/runtime dependency were removed; auth, asset, group/share, user,
  notification, and websocket modules compile for Android and iOS through the
  same client boundary.
- Asset and financial-list transport implementations are now owned and
  compiled from `data:portfolio`. Group/share transport and social repository
  implementations are owned by `data:social`. The former account, cash,
  insurance, investment, building, land, liability, group, and websocket API
  projects are retired from `settings.gradle.kts`; their original source trees
  remain preserved but are no longer active compile inputs.
- Auth endpoint transports are now owned and compiled from `data:auth`. The
  former `functional:api:auth-api` project is retired from the settings/build
  graph; active transport and mapper packages use `com.wealthvault.data.auth.*`,
  while the original source tree remains as an uncompiled compatibility
  archive.
- Auth transport interfaces and implementations are internal to `data:auth`,
  so the shared framework exports domain contracts rather than endpoint
  details while keeping the backend contract unchanged.
- The shared authenticated Ktor client and single-flight refresh coordinator are
  now owned and compiled from `base:network`; the former
  `functional:api:setup-api` project is retired from the graph and active
  networking packages use `com.wealthvault.network`, while its original source
  is retained only in the uncompiled archive.
- Google provider adapters now compile from `data:auth`, and LINE/session
  adapters compile from `base:security`, with their existing Android SDK and
  GoogleSignIn CocoaPod bindings. Active packages use `data.auth.google`,
  `security.line`, and `security.session`; the standalone provider projects are
  retired from the graph and their source/test directories remain uncompiled
  archives.
- Dashboard and notification have domain DTO mappers, stale-cache fallback,
  explicit refresh, immutable UI state, repository tests, and fixed-point
  dashboard amounts. Dashboard cache reads migrate the legacy numeric payload.
- Portfolio mutation invalidation clears the portfolio and reference-list
  namespaces in one SQLDelight transaction, so a successful backend mutation
  cannot leave a half-invalidated persistent cache.
- Portfolio list/detail reads use a five-minute stale-while-revalidate cache,
  explicit force refresh, mutation invalidation, and observable freshness
  streams. Profile reads use the same cache contract with user/friend cache
  invalidation and offline fallback; social friends/groups/pending-friends use
  the shared cache boundary, expose explicit `refresh(force)`, and keep
  remaining operations on the compatibility adapter.
- Land, building, and insurance reference lists now use the shared persistent
  cache with a fifteen-minute TTL, single-flight refresh, and stale fallback so
  offline forms do not repeatedly hit the network.
- Cache readers treat future-dated and overflow-prone persisted timestamps as
  expired instead of allowing corrupt or clock-skewed snapshots to suppress a
  refresh for one full TTL window; shared timestamp and reference-cache tests
  cover both cases.
- The former `features:manage:form` compatibility surface remains preserved in
  the unregistered `base:financial-common` source archive; no active feature or
  composition-root module depends on it.
- The active `features:manage:financialList` module no longer links the
  compatibility financial-common presentation bundle. Its form fields,
  attachment picker, dropdowns, and map-type options now live inside the
  feature, while every add-entry route uses typed `SharedScreen` destinations.
  The old presentation bundle is retained only as source for rollback and is
  not registered in `settings.gradle.kts`.
- Profile now exposes `domain:profile` contracts. API DTOs are mapped inside
  `data:profile`'s `ProfileDataSource`, and LINE/device actions use capability
  interfaces.
- Dashboard transport, fixed-point mapping, and stale-while-revalidate cache
  now live in `data:dashboard`; the dashboard feature contains only UI and
  ScreenModel bindings.
- Notification repository, mutation, unread-badge, and friend-request
  adapters now compile from `data:notification`; the former functional
  notification project is retired from the graph and its Android/iOS
  push-service sources now live under `base:security`.
- Push token acquisition is exposed as the `domain:auth`
  `PushNotificationProvider`; Firebase/APNs adapters bind that contract from
  the platform module, so auth presentation no longer imports push-service
  implementation packages.
- The remaining financial-common asset/reference network adapters now live in
  `data:portfolio`; their old package paths are compatibility adapters inside
  the data boundary. Cache invalidation coverage is tested inside the data
  module rather than through a feature implementation package.
- Social now has a `domain:social` model boundary. Friend requests, pending
  friends, friend profiles, asset previews, and friend messages are mapped in
  `SocialDataSource`; notification friend actions use the same contracts.
- Portfolio and social asset amount fields now use `Money` in domain models;
  investment quantities, investment prices, and liability interest rates use
  `FixedDecimal`/`Money` in both read models and command contracts. Conversion
  to legacy wire strings/numbers is limited to data adapters and presentation
  text-input boundaries.
- Social presentation now depends on the domain `SocialRepository` contract,
  while `SocialRemoteDataSource` isolates the API-backed implementation. Friend,
  group, and pending-friend reads expose cache freshness streams and have
  Android-host/iOS-simulator cache tests. The implementation and Koin boundary
  now live in `data:social`, not in the feature module.
- Social presentation now depends on the domain `GroupChatGateway` and
  `SessionManager` contracts. Ktor WebSocket transport now lives in
  `data:social`, and secure-session storage remains behind data/platform
  adapters rather than leaking into UI.
  The former `functional:api:websocket-api` project is now retired from
  `settings.gradle.kts`; its original transport source is retained only as an
  uncompiled compatibility archive while the social data module owns the Ktor
  client and DI binding.
- Typed Voyager destinations now live in `base:core` as navigation contracts;
  feature and compatibility modules no longer depend on the `main` app module.
  The old `navigation-point` source is retained only as a source-compatible
  typealias for downstream consumers.
- Authentication/session consumers now use the `domain:auth` session contract;
  the DataStore-backed `TokenStore` is bound only as the composition-root
  adapter. Presentation sees status, user identity, push metadata, and clear
  operations without importing storage DTOs.
- Notification read and mutation contracts now live in `domain:notification`;
  the feature data package remains only as a compatibility alias.
- Login and registration now consume `domain:auth` contracts; API request/response
  types stay inside their data implementations.
- Auth recovery/provider and push-device adapters now pass primitive/domain
  values across API interfaces; wire request DTOs are constructed only inside
  the API implementations. The unused introduction transport/repository
  adapter was removed, leaving the screen model on `ProfileRepository`.
- The unreferenced compatibility model duplicates that still carried `Double`
  monetary fields were removed; portfolio and obligation forms now use the
  shared domain `Money`/`FixedDecimal` contracts end to end.
- Shared date-picker and compatibility financial-list code now use the Kotlin
  time `Instant` API with `kotlinx-datetime` 0.7.1. This removes the older
  datetime ABI from the active Android/iOS link and keeps date conversion
  behavior unchanged.
- Provider/device/password-recovery onboarding flows and the legacy
  financial-common/financial-list ScreenModels now inject domain contracts. The
  strict scan reports zero presentation imports of `RepositoryImpl`.
- `AppCoordinator` owns authentication routing; features no longer replace the
  root screen in response to lifecycle callbacks.
- All `ON_RESUME` observers and production `println`/`print` calls have been
  removed from the migrated source tree.
- Release shrinking, APK size verification, architecture ratcheting, and
  Android/iOS CI workflow are present.
- The architecture ratchet also checks active `data/*` transport, wire, and
  API-model source files for public DTO declarations. The current
  `public_data_transport_declarations` count is zero, so DTO visibility cannot
  silently widen when a new endpoint is added.
- The ratchet also rejects direct Ktor/JSON transport imports from feature
  source. Notification metadata and group-chat WebSocket envelopes are mapped
  inside their data adapters and exposed to presentation as typed domain
values; the current `feature_transport_serialization_imports` count is zero.
- Feature Gradle modules also cannot carry transport serialization, Ktorfit, or
  DataStore dependencies; `feature_transport_serialization_dependencies` must
  remain zero.
- Domain source and domain Gradle modules cannot depend on Kotlin/Ktor
  serialization. Cache snapshots and wire DTOs stay private to data modules;
  `domain_transport_serialization_imports` and
  `domain_transport_serialization_dependencies` must remain zero.
- Domain modules communicate through `core:model` and contracts rather than
  depending directly on another bounded-context domain module;
  `domain_to_domain_dependencies` must remain zero.
- The device-registration mutation result follows the same shared-model rule:
  auth and notification adapters use `core:model`, while the old notification
  package name remains a source-compatible alias for downstream callers.
- The ratchet also blocks new feature build edges to the retained
  `base:financial-common` compatibility presentation bundle; its current
  `feature_to_compatibility_dependencies` count is zero.
- Data bounded contexts have no direct data-module dependency edges
  (`data_to_data_dependencies=0`); cross-context behavior travels through
  domain contracts or the composition root.
- The retained financial-common compatibility bundle also has no direct data
  dependency (`compatibility_to_data_dependencies=0`); its screen models only
  consume domain contracts while implementations are assembled at the root.
- The `benchmarks` Android test module contains five-iteration cold/warm startup
  Macrobenchmarks with startup, frame-timing, and peak-memory metrics plus a
  Baseline Profile collection test. The root
  `verifyPerformanceBudgets` task validates the shared budget schema once a
  runner writes measured properties.
- Every feature and financial-common compatibility `ScreenModel` now exposes a
  typed `StateFlow<UiState<...>>`; the 17 legacy asset/obligation summary and
  form models retain their old flows as compatibility views while typed actions,
  error state, and one-time effects are introduced through the shared UDF
  contracts. `verifyArchitecture` scans all production `ScreenModel` files and
  ratchets `screen_model_udf_gaps` to zero.
- `verifyArchitecture` now also enforces a hard 35-module Gradle graph budget;
  the current settings graph contains 26 registered modules.

## Verification

```shell
./gradlew verifyArchitecture -PstrictArchitecture=true
./gradlew :base:core:testAndroidHostTest :base:core:iosSimulatorArm64Test
./gradlew :base:security:testAndroidHostTest :base:security:iosSimulatorArm64Test
./gradlew :data:auth:testAndroidHostTest :data:auth:iosSimulatorArm64Test
./gradlew :data:dashboard:testAndroidHostTest :data:dashboard:iosSimulatorArm64Test
./gradlew :data:profile:testAndroidHostTest :data:profile:iosSimulatorArm64Test
./gradlew :data:portfolio:testAndroidHostTest :data:portfolio:iosSimulatorArm64Test
./gradlew :data:social:testAndroidHostTest :data:social:iosSimulatorArm64Test
./gradlew :base:network:testAndroidHostTest :base:network:iosSimulatorArm64Test
./gradlew :features:auth:login:iosSimulatorArm64Test
./gradlew :domain:notification:testAndroidHostTest :domain:notification:iosSimulatorArm64Test
./gradlew :features:dashboard:testAndroidHostTest :features:dashboard:iosSimulatorArm64Test
./gradlew :features:notification:testAndroidHostTest :features:notification:iosSimulatorArm64Test
./gradlew :features:profile:testAndroidHostTest :features:profile:iosSimulatorArm64Test
./gradlew :features:social:testAndroidHostTest :features:social:iosSimulatorArm64Test
./gradlew :features:manage:financialList:testAndroidHostTest :features:manage:financialList:iosSimulatorArm64Test
./gradlew :composeApp:linkPodDebugFrameworkIosSimulatorArm64
./gradlew :androidApp:verifyReleaseApkSize
```

The CI commands select the Android host and `iosSimulatorArm64` targets
explicitly. Running the aggregate `allTests` task locally also schedules the
unused iOS x64 targets and can exhaust a 4 GiB Kotlin/Native compiler daemon.

The architecture task is a ratchet: it prevents regressions immediately. The
feature-to-feature, feature-to-data, feature-to-infrastructure, and
feature-to-app edges, plus forbidden domain edges, are now zero; all API DTO
imports are confined to data/transport source roots, and
presentation sources are scanned across all modules including compatibility
code and direct implementation imports.
The same ratchet now rejects any Gradle source-set edge back into the preserved
`functional/*` archives; those files remain available for rollback but are not
active compile inputs.

The latest auth/device/cash/account/insurance/liability/investment/share/group/land/building/user transport cleanup
reduces feature and compatibility API-model imports to zero (from the checkpoint
baseline of 125). Portfolio, social, dashboard, friend, and reference-list
adapters now expose only domain/core contracts; their legacy wire DTOs are
internal to the API boundary.

The profile slice now owns its private Ktor wire DTOs and multipart transport
inside `data:profile`; the feature no longer depends on the legacy
`functional:api:user-api` module. Friend-request acceptance follows the same
boundary in notification/social and exposes only domain results to callers.
Social friend-list reads now reuse the profile domain repository/cache instead
of issuing a second legacy friend API request.
The financial-list friend directory now uses the same profile contract and no
longer depends on `functional:api:user-api`.
The financial-list group directory now reuses `SocialRepository`, so its group
summary mapper no longer imports `group-api` DTOs. The financial-list
repository and mutation adapters now live in `data:portfolio`, while group and
share transport types are owned by `data:social`.
The legacy `functional:api:user-api` project was retired from
`settings.gradle.kts` after its final consumers were migrated. Its source
directory is retained as a compatibility archive for this checkpoint, but it
is no longer compiled or registered in Koin; new code must use the
profile/social/dashboard domain contracts and data transports instead.
The group-list transport adapter now maps wire DTOs to `GroupSummary` before
crossing the API boundary; both social and the compatibility financial bundle
consume the domain model directly.
The land transport adapter now maps wire DTOs to portfolio land contracts;
financial-list, social, and the compatibility financial bundle consume only
domain land models, with cache snapshots kept local to the data implementations.

Latest local verification on `optimize-v1` also links the iOS simulator
framework and runs the configured shared `iosSimulatorArm64Test` targets,
including `data:auth`, `data:portfolio`, `data:social`, and `base:network`; the Android host
suite and release/benchmark assembly pass as well.
Runtime budgets remain pending until
Macrobenchmark and XCTest/signpost measurements are collected on the fixed
self-hosted runners described in `performance-baseline.md`.

The current checkpoint is intentionally not marked as complete for the full
10/10 target: iOS native coverage and the real five-run Android/iOS performance
samples still require the fixed self-hosted runners. Android authenticated
navigation is now covered by a connected smoke test that exercises the main
feature routes and secure logout; the remaining runner work is kept explicit
rather than hidden behind a green compile gate.
