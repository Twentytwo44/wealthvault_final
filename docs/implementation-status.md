# Implementation status

This is the current checkpoint for `optimize-v1`. It separates code changes
that are verified locally from acceptance items that require a fixed Android
device or iOS simulator.

## Complete and verified

- Strict dependency/import ratchet passes with zero feature-to-feature edges,
  including the source-level sibling-feature import check,
  zero feature source-root edges,
  zero feature-to-data/infrastructure edges, zero presentation API/legacy model
  imports, zero lifecycle resume observers, zero production `print`/`println`
  calls, zero presentation/session-storage imports, zero domain infrastructure
  imports, zero data-to-feature/app dependencies or presentation imports, and
  zero public legacy API model declarations (except the intentional LINE
  bridge type), and
  zero ScreenModels missing the shared UDF state contract
  (including the 17 compatibility summaries/forms). Qualified API references
  outside data sources are also rejected, not only ordinary imports.
- The same ratchet checks active `data/*` transport, wire, and API-model files
  for public DTO declarations; `public_data_transport_declarations` is zero.
- Feature source is also checked for direct Ktor or JSON-serialization imports;
  `feature_transport_serialization_imports` is zero. Notification metadata is
  decoded by `data:notification` and group-chat frames/commands are decoded and
  encoded by `data:social`, so Compose screens consume domain values only.
- Feature Gradle modules no longer carry transport serialization, Ktorfit, or
  DataStore dependencies; the ratchet reports zero
  `feature_transport_serialization_dependencies`.
- Domain source and Gradle modules no longer depend on Kotlin/Ktor
  serialization. Profile and social cache records are data-local DTOs with
  explicit mappers, and the architecture ratchet reports zero
  `domain_transport_serialization_imports` and
  `domain_transport_serialization_dependencies`.
- Cross-context read models used by social now live in `core:model`; the
  profile/portfolio domain modules retain compatibility typealiases, and
  `domain_to_domain_dependencies` is zero.
- Data modules have no direct data-module edges (`data_to_data_dependencies=0`);
  auth device registration owns its transport adapter instead of reaching into
  notification data.
- The compatibility UI bundle has no direct data-module edge
  (`compatibility_to_data_dependencies=0`); data implementations stay in the
  composition root.
- Registration sources now live under the active auth/login feature module;
  the former unregistered `features/auth/register` source-root bridge was
  removed, so the auth vertical has one Gradle/presentation boundary.
- Portfolio data adapters no longer compile under the historical
  `financiallist.data` or `wealthvault_final/financial-*` namespaces. The
  active repository/form adapters and transport files now use the bounded
  `com.wealthvault.data.portfolio.*` namespace; the architecture ratchet still
  reports zero legacy namespace references and zero public DTO declarations.
- Push-device registration is now launched from an application-scoped
  composition-root job instead of a navigation child scope, so a slow token
  provider cannot be cancelled when the splash/login route is replaced; a
  second authenticated emission cannot start a duplicate registration job.
- The security composition root now binds only the domain `SessionManager` and
  `SessionTokenStore`; the duplicate legacy session interfaces/value objects
  were removed, leaving one observable session state contract for UI/networking.
- The ratchet also reports zero active source-root edges into preserved
  `functional/*` archives, so future modules cannot silently compile legacy
  transport trees outside their owning `data`/`base` bounded context.
- Active transport, wire-mapper, network, Google, LINE, session, and push
  packages no longer use the historical `*_api`, `data_store`, or provider
  namespace names; a source scan confirms zero legacy package references in
  compiled modules.
- The root `verifyDatabaseMigrations` gate runs SQLDelight's migration verifier
  and is included in both architecture and Android CI jobs, so schema upgrades
  are checked before feature tests run.
- Domain money uses `Money` minor units; investment quantities and liability
  rates use `FixedDecimal`. Those command requests keep fixed-point types until
  the API mapper formats legacy wire values. Core arithmetic, rounding, and
  command-mapper tests pass on Android and iOS, including the signed
  `Long.MIN_VALUE` decimal boundary. Real-estate area fields remain a
  compatibility follow-up because the existing UI/backend contract uses
  decimal doubles there.
- Ktor clients use shared public/authenticated clients, idempotent retry rules,
  redacted logging, and single-flight refresh.
- Google sign-in now exposes only the domain `GoogleIdentity` from the data
  boundary; provider profile fields stay inside the platform adapter, and
  Android credential cancellation propagates instead of being reported as a
  failed sign-in.
- A transient refresh outage no longer forces logout; the secure session is
  cleared only for an explicit 401/403 refresh rejection or an unusable token
  response. Network exceptions and 5xx responses preserve the current session
  and are covered by Android-host/iOS-simulator boundary tests.
- The shared client now selects CIO on Android and Darwin on iOS through a
  platform engine boundary; common code no longer hard-codes an engine.
- Session migration uses Android Keystore/iOS Keychain with read-back
  verification and retry-safe fallback.
- Session token writes and clears are serialized behind one mutation mutex, so
  refresh, login, and logout cannot interleave secure-storage operations; the
  concurrent-write regression test passes on Android host and iOS simulator.
  Token-pair writes also rollback on a partial secure-storage failure, so a
  failed refresh cannot leave access and refresh tokens from different
  sessions. Logout uses the same rollback boundary: a failed access or
  refresh-key removal leaves the previous secure pair and identity intact
  instead of creating a half-cleared session; this regression is covered on
  Android host and iOS simulator. Logout removes only the session keys and
  preserves secrets owned by other security capabilities.
- Secure session mutations now commit a verified length-prefixed token pair
  before removing legacy preferences. On restart, that pair wins over stale
  plaintext left by an interrupted cleanup (including a committed signed-out
  pair), while the old individual secure keys remain readable for migration
  compatibility.
- Logout now removes only access/refresh tokens and the authenticated user id
  from preferences; push-device metadata and unrelated preferences survive the
  session boundary, so a later login can reuse the platform token without
  destructive DataStore clearing. This behavior is covered by Android-host and
  iOS-simulator session tests.
- Android Auto Backup and device-transfer rules exclude both the Keystore-backed
  secure-session preferences and the legacy DataStore token file, preventing a
  token snapshot from being transferred to another installation.
- The local release keystore is now ignored and removed from the Git index while
  remaining on the developer machine; release signing is opt-in through Gradle
  properties/environment variables, so source changes no longer carry signing
  material.
- Dashboard, notification, portfolio, profile, social, and reference-list
  caches use stale-while-revalidate behavior. Reference lists use a 15-minute
  TTL; successful mutations invalidate portfolio and reference namespaces.
  Future-dated cache timestamps are treated as expired, so clock skew or a
  corrupted snapshot cannot suppress the next refresh.
  Social now also exposes the shared `refresh(force)` contract so friends,
  groups, and pending-friend read models can be refreshed explicitly without
  bypassing their single-flight/cache paths.
- The usable-flow checkpoint now invalidates both cache namespaces after every
  portfolio create/update/delete path, isolates cache-clear failures, and
  prevents cancellation from being swallowed. Dashboard and notification
  cache decode failures fall back to the network instead of breaking the
  screen, while future-dated cache timestamps cannot suppress a refresh.
- Cache age arithmetic is now overflow-safe across dashboard, notification,
  portfolio, profile, social, and reference-list repositories. Corrupted
  timestamps such as `Long.MIN_VALUE` are treated as stale and covered by
  shared regression tests instead of being misclassified as fresh.
- Portfolio mutations now clear the portfolio and reference-list namespaces
  through one SQLDelight transaction; lightweight test caches retain a
  compatibility fallback while the persistent store guarantees no partial
  invalidation.
- Refresh and submit actions in notification, asset, debt, and profile flows
  are single-flight. Login, social group/share, profile-save, and share-setting
  actions ignore duplicate taps; social mutation failures now propagate to the
  error state instead of being silently ignored.
- Notification read mutations now invalidate the persistent snapshot only in
  the data repository; the ScreenModel updates its in-memory UDF state without
  issuing a second cache clear, avoiding duplicate disk work after each tap.
- The shared suspending `AppResult` wrapper now propagates coroutine
  cancellation instead of turning a disposed screen into an `Unknown` error;
  Android-host and iOS-simulator core tests cover the lifecycle regression.
- Login and Google sign-in now share one guarded authentication job, profile
  checks rethrow cancellation, and unexpected failures always release the
  loading state. Password recovery (OTP, verification, and reset) is likewise
  single-flight with cancellation-safe cleanup; push-device registration,
  registration, and onboarding submissions no longer leave a screen stuck
  behind an exception or enqueue duplicate work.
- Profile editing and sharing settings now guard fetch/save/close-friend
  actions against duplicate taps and reset loading state on cancellation or
  unexpected repository failures, including optimistic close-friend rollback.
- Logout now treats remote device unregister as best effort and clears the
  local secure session in a non-cancellable section. Offline failures or
  profile-screen disposal can no longer trap the user in an authenticated
  session; a genuine Keystore/Keychain clear failure is surfaced instead of
  emitting a false logout success.
- Dashboard, notification, asset, and liability list loads now recover from
  unexpected repository exceptions instead of leaving a permanent spinner;
  delete/share-target actions also release their in-flight guards reliably.
- Dashboard and notification first-load failures now render a localized error
  state with an explicit retry action, so a temporary offline launch does not
  leave an empty screen with no recovery path.
- Asset/debt lists, profile, and social friend/group lists now render the same
  loading/error/retry states instead of silently showing an empty screen when
  the first request fails.
- Asset/debt lists now explain the successful empty state and offer a direct
  add action; friend/group lists explain the empty state and show a clear
  no-results message when a search filters every item out.
- Share-asset selection now exposes loading and retryable failure states,
  disables duplicate submit taps while saving, and keeps a previously shared
  recipient visible until its shared-item lookup succeeds, so a failed
  unshare cannot silently lose the user's pending change.
- Share-asset loading now also maps previously shared external email targets
  back into the selection state, preventing a later save from unintentionally
  clearing existing email invitations.
- Add-friend now distinguishes the initial pending-request load from a
  successful empty list and exposes a retry action when that load fails.
- Group create/edit now renders friend-list loading and failure states with an
  explicit retry action, and keeps save disabled until the member source is
  ready so a failed lookup cannot accidentally submit an incomplete edit.
- Group profile, friend profile, and group chat now show an actionable retry
  state on initial load failures; group chat also shows a real loading indicator
  instead of a blank panel while messages are being fetched.
- Authentication routing now belongs to the composition-root `AppCoordinator`:
  login and Google sign-in only update the session, while the coordinator
  chooses login, onboarding, or main content from the session and profile
  state. This removes feature-owned global navigation and keeps transient
  profile failures usable through the authenticated dashboard.
- The profile Compose route receives its LINE provider through a
  composition-root factory; the feature no longer depends on `base:security`.
  The active LINE adapter uses `com.wealthvault.security.line`, while only the
  original source archive retains the historical package; endpoint adapters
  themselves are outside feature source roots.
- The composition-root LINE factory is a concrete implementation rather than a
  nested callback lambda, removing the Kotlin/Native export diagnostic while
  keeping provider callbacks outside the profile feature boundary.
- Push-device registration now follows the authenticated session in the
  composition root, with a bounded token wait and best-effort backend retry
  behavior. Login no longer performs a duplicate profile lookup or owns a
  ScreenModel-scoped registration job that could be cancelled during routing.
- Android FCM token rotation now persists through the session boundary and
  re-registers with the backend while authenticated; failures are logged with
  redacted context and retried on the next authenticated startup.
- iOS FCM token rotation now uses the same session-owned registration path:
  AppDelegate persists the host token first, submits it immediately when the
  shared graph is ready, and otherwise lets authenticated startup retry it.
  Signed-out refreshes are retained locally without calling the backend.
- Notification read and mark-all mutations now deduplicate concurrent taps and
  release their guards after success, failure, or cancellation.
- Group chat message loading, silent WebSocket refresh, JOIN/connection
  cancellation, and grant-access failures now update error state without
  leaving a chat screen permanently loading or crashing its child coroutine.
- Friend profile, friend-space, and add-friend/pending-request actions now
  release loading state on unexpected failures and cancellation while keeping
  their optimistic UI state consistent.
- Every migrated asset and liability form now has a guarded submit job, so a
  rapid double tap cannot create/update the same record twice and the UDF
  loading state is set before the network coroutine starts.
- Financial asset and liability forms now collect that UDF state at the route:
  failed saves render a localized error banner, duplicate submit is disabled
  while the request is active, and the button communicates the save progress.
- The compatibility financial-common summary forms now use the same guarded
  submit/cancellation contract, and land/building/share reference loaders
  surface repository failures instead of silently leaving stale loading state.
- Social friend/group/share screens now guard fetch and mutation jobs, preserve
  cancellation semantics, and close group-chat sessions with a bounded
  best-effort LEAVE/close cleanup during navigation.
- Social list/profile/chat read paths now share one in-flight job per
  ScreenModel, explicit social refresh actions bypass the repository TTL, and
  WebSocket reconnects do not create a second active connection. Successful
  notification and social mutations keep cache invalidation best-effort so a
  storage outage cannot turn a completed backend mutation into a UI failure.
- All create/update portfolio form repositories, including investment update,
  are bound in the data composition root. Land edit mapping uses the deed
  number field, and login no longer adds an artificial delay before routing.
- Profile and friend-request transport DTOs are now private to their data
  adapters; profile and financial-list friend reads no longer depend on the
  legacy user API module.
- Portfolio delete responses are now reduced to a domain `Boolean` inside the
  data adapter; the former `DeleteBaseResponse` API DTO no longer lives in
  `core` or crosses the repository boundary.
- Notification responses and group-list responses now map to core/domain
  models inside their transport adapters; the feature and compatibility
  callers no longer see those wire DTOs.
- Cash list/detail/create/update/delete now expose portfolio domain contracts;
  legacy cash wire DTOs are internal to `functional:api:cash-api` and cash
  adapter mapping is covered by iOS simulator tests.
- Account list/detail/create/update/delete now expose portfolio domain
  contracts; account wire DTOs are internal to `functional:api:account-api`
  and mapping is covered by iOS simulator tests.
- Insurance list/detail/create/update/delete now expose portfolio domain
  contracts; insurance wire DTOs are internal to `functional:api:insurance-api`
  and reference-list caching uses a local serializable snapshot.
- Liability list/detail/create/update/delete now expose portfolio domain
  contracts; liability wire DTOs are internal to `functional:api:liability-api`
  and principal/interest mapping uses the shared fixed-point contracts.
- Investment list/detail/create/update/delete now expose portfolio domain
  contracts; investment wire DTOs are internal to `functional:api:investment-api`
  and quantity/price mapping uses `FixedDecimal` and `Money`.
- Share selection, target lookup, share/unshare mutations, and friend/group
  share reads now expose `domain:social` contracts; legacy share DTOs remain
  internal to the shared group API adapter.
- Group creation/detail, membership, access, chat messages, and lifecycle
  mutations now expose `domain:social` contracts; group wire DTOs are internal
  to `functional:api:group-api`.
- Land list/detail/create/update/delete now expose portfolio domain contracts;
  land wire DTOs are internal to the `data:portfolio` transport package, and
  land reference caching uses local serializable snapshots.
- Building list/detail/create/update/delete, dashboard, friend search/list,
  pending friends, friend profile, and friend messages now expose domain/core
  contracts; their user/building wire DTOs are internal to the API adapters.
- User profile, close-friend reads, and profile mutations now also expose only
  `domain:profile` values; all remaining user transport DTOs are internal and
  their Android-host/iOS-simulator boundary tests cover profile and mutation
  mapping.
- Group chat WebSocket transport now lives inside `data:social` and is bound
  with the domain `GroupChatGateway`; the social feature keeps only screen
  models and UI bindings, and the legacy websocket API project is no longer
  registered or included in the app composition root.
- The WebSocket boundary is typed end-to-end: `GroupChatEvent` and
  `GroupChatAction` are domain contracts, while frame envelopes and command
  JSON remain private wire models in `data:social`. Notification invite
  completion metadata follows the same rule and is mapped to
  `NotificationItem.isCompleted` before it reaches UI.
- Account, cash, insurance, investment, building, land, and liability
  transports plus the financial-list repositories now compile through
  `data:portfolio`; their legacy API project edges were removed from the
  settings/build graph without changing the existing package contracts.
- Group/share endpoint transports and the social repository/cache adapters now
  compile through the dedicated `data:social` module. The social feature owns
  only screen models and WebSocket composition, while `data:social` exposes
  `SocialRepository` and keeps the legacy wire packages internal to data.
- Auth endpoint transports now compile from `data:auth`; the legacy
  `functional:api:auth-api` project edge was removed and active transport,
  wire-mapper, and provider packages now use `com.wealthvault.data.auth.*`.
  The original source tree remains preserved but is no longer an active source
  root.
- The shared authenticated Ktor client and refresh coordinator now compile
  from `base:network`; the legacy `setup-api` project edge was removed and
  active networking packages now use `com.wealthvault.network`. Its original
  source remains only in the uncompiled archive.
- Google provider adapters now compile from `data:auth`, and LINE/session
  adapters compile from `base:security`, retaining the Android credential,
  LINE SDK, and iOS GoogleSignIn bindings while removing standalone provider
  project edges. Active packages use `data.auth.google`, `security.line`, and
  `security.session`; the old underscore/package-name variants are absent from
  active sources.
- Auth repositories for login, registration, password recovery, OTP, reset,
  Google provider login, and device registration now live in `data:auth`;
  auth features only register presentation/use-case bindings and consume
  `domain:auth` contracts. Google interactive sign-in is exposed through a
  domain `GoogleSignInProvider`, so the UI no longer imports the provider SDK
  facade.
- Auth persistence now consumes the domain `SessionManager` and
  `SessionTokenStore` contracts rather than the legacy `data_store` interfaces.
  The secure-session implementation is exposed to Android/iOS composition roots
  through `base:security` facades under `com.wealthvault.security.session`, and the architecture ratchet rejects direct
  storage-package imports in presentation, domain, and app wiring.
- The platform LINE sign-in adapter now lives behind the `base:security`
  boundary; profile presentation consumes the domain capability wrapper and
  architecture CI rejects direct feature-to-data module dependencies.
- Push token acquisition now uses the domain `PushNotificationProvider`; the
  auth feature no longer depends directly on the platform notification module.
- Dashboard and profile transport/repository implementations now compile in
  dedicated `data:dashboard` and `data:profile` modules; their feature modules
  retain only presentation and feature-level DI bindings. Repository/cache
  tests moved with the data implementations and pass on Android host and iOS
  simulator targets.
- The remaining financial-common asset/reference adapters (cash, account,
  investment, land, building, insurance, and liability) now compile in
  `data:portfolio`; their old package paths are compatibility adapters inside
  the data boundary. Cache invalidation tests moved with the data implementation
  and pass on Android host and iOS simulator targets.
- Notification cache, mutation, unread-badge, and friend-request adapters now
  live in `data:notification` behind `NotificationDataModule`; the
  former `functional:notification` project is no longer registered; its
  platform push-service sources compile behind `base:security`, and the
  notification feature keeps only screen/use-case bindings. Cache and
  friend transport tests moved with the data implementation and run on the
  iOS simulator.
- The former `user-api`, auth/setup/provider API, `functional:data-store`, and
  `functional:notification` projects are no longer registered in the active
  build graph. Their source-compatible archives remain only for rollback;
  active session/push implementations compile behind `base:security`.
- Android host tests and affected iOS simulator tests pass. The latest release
  APK is 25,988,184 bytes, below the 35 MB budget. Benchmark source/assembly
  passes with startup, frame-timing, and peak-memory metrics enabled; the
  release-size task also exports a measured artifact property for CI.
- Generated Android `ExampleInstrumentedTest` placeholders were replaced with
  feature UDF/state contract checks, Keystore round-trip coverage, and provider
  construction smoke tests; the active main-navigation device test now checks
  the typed Voyager destination contract as well. The device-test convention now supplies
  `kotlin-test`, so inherited common tests compile in the device source set.
- The pull-request matrix now includes the dedicated `data:social` Android
  host and iOS simulator tests alongside the migrated auth, portfolio, network,
  profile, notification, social, and financial-list slices.
- Android CI now compiles every active KMP device-test contract (including the
  typed main-navigation smoke test) before the connected production smoke;
  modules without device-test resources no longer fail on an unconfigured
  Compose Resources copy task.
- The root `verifyUsability` gate now packages the deterministic checkpoint in
  one reusable task: architecture, SQLDelight migrations, Android host tests,
  active device-test compilation, debug compilation, and release APK checks.
  Android CI invokes this gate before coverage and connected smoke tests;
  runtime benchmarks remain outside it by design.
- The matching `verifyUsabilityIos` gate links the Compose framework and
  discovers every active `iosSimulatorArm64Test` task, so the iOS CI matrix
  cannot silently omit a newly migrated shared module.
- After the final active portfolio namespace move and route/content extraction,
  local `verifyUsability` and `verifyUsabilityIos` both pass. The Android
  release artifact remains 25,988,184 bytes and the strict architecture gate
  reports zero violations, including `large_screen_route_files=0`.
- The iOS shared-test matrix now includes the `main` navigation contract so
  typed destination coverage stays cross-platform with the Android smoke path.
- Ordinary pull requests now block on architecture, migrations, Android/iOS
  builds, shared tests, connected Android smoke, and strict measured Kover
  coverage before any optional performance work. Full five-sample runtime and
  build-performance acceptance remains available through the manual
  `run_performance` workflow input, so deferred benchmarking cannot hide or
  prevent a usable application checkpoint from merging.
- The build now provides `collectPerformanceMetrics` and
  `collectCoverageMetrics` tasks. They merge runner exporter properties,
  reject missing/conflicting values, validate sample counts and percentages,
  and never synthesize metrics before the strict gates consume them.
- Performance exporters now recurse through downloaded artifact directories,
  reject placeholder/non-finite values, and record five-run Gradle timing
  medians for Android configuration/incremental/clean builds and iOS framework
  linking. Release APK size is preserved before the clean-build probe.
- Kover 0.9.9 is applied by the migrated KMP library convention. The aggregate
  `koverXmlReportAndroidAll` task generates Android host-test XML for every
  migrated module, and `tools/export_kover_coverage.py` converts measured line
  and branch counters into the canonical coverage properties without filling
  missing values. The latest local strict coverage gate passes for the
  migrated production scope: overall 88.239%, data 87.311%, domain 92.835%,
  and reducer/state contracts 92.308%; Money 95.614%, auth 96.774%, session
  refresh 100%, and cache migration 100%. Generated DTO/model accessors,
  legacy compatibility packages, and the not-yet-migrated social transport
  are intentionally outside this staged gate until their vertical migrations
  are complete.
- The architecture ratchet also rejects new `Double`/`Float` fields for
  monetary amounts, prices, balances, quantities, and rates in domain models;
  real-estate `area` remains an explicitly documented physical-measurement
  compatibility field.
- Active network and bounded-context data modules no longer depend on Ktor's
  unused `Auth` plugin; authentication is handled by the shared `HttpSend`
  interceptor and single-flight session refresh boundary. The catalog alias is
  retained only for uncompiled compatibility archives.
- The qualified global `Json` serializer is now owned by the network
  composition root. Endpoint modules no longer register duplicate singletons
  with the same Koin key, so decoding policy is stable regardless of module
  load order and cache/repository adapters reuse the same instance.
- iOS Keychain writes now handle an add/update race by replacing the duplicate
  item and still verifying the value through the existing session migration
  read-back contract.
- Dashboard list-row mapping now lives in an immutable, unit-tested UI mapper;
  the Compose screen only remembers the mapped rows and renders them, keeping
  category/label/formatting decisions out of recomposition.
- Dashboard presentation is now split into a 233-line route/content file and a
  separate reusable card/component file; navigation and backend contracts are
  unchanged.
- The remaining large financial-list and social routes now keep navigation and
  lifecycle wiring in sub-300-line `*Screen.kt` files, with rendering moved to
  same-package `*Content.kt` files. A strict `large_screen_route_files=0`
  ratchet prevents the monolithic route pattern from returning.
- Notification presentation is likewise split into a 219-line state/content
  route and reusable notification-card components; loading, stale-cache,
  retry, friend-request navigation, and mark-as-read behavior remain in the
  route contract.
- Login presentation is now split into a 202-line route/content file with
  dedicated field and action components. Credential validation is a shared
  pure contract with Android-host/iOS-simulator coverage, so Compose no longer
  allocates an email regex on every submit and login behavior remains unchanged.
- Profile presentation now keeps share-setting orchestration in the route while
  rendering lives in a dedicated `ShareSettingBody`; the edit-profile text
  field is reusable and the date conversion contract is isolated and tested.
  Profile Android-host/iOS-simulator tests cover the new date edge cases,
  including rejecting malformed hyphenated input instead of truncating it.
- Profile settings now render an explicit retry action when the initial user
  or close-friend load fails; edit-profile loading and failure states are also
  visible instead of leaving an empty form while the repository is unavailable.
- Edit-profile date selection is isolated in a reusable date-picker component,
  keeping ISO/Thai conversion and picker UI out of the route's form layout.
- Registration now uses the same pure validation boundary from both the route
  and `RegisterScreenModel`, with Android-host/iOS-simulator coverage for
  required fields, email format, and password confirmation. Its form is split
  from the route while the merged auth module keeps the existing navigation and
  backend contract.
- Onboarding now shares one required-field validator between its enabled-state
  UI and `IntroScreenModel`, with cross-platform tests. A recreated onboarding
  route also restores the localized birthday display from the ISO state instead
  of showing a blank date field.
- Onboarding rendering is now split into a 204-line route/date-dialog file,
  `IntroForm`, and a dedicated profile text-field component; image picking,
  date selection, and navigation callbacks remain at the composition boundary.
- Financial-list presentation no longer depends directly on
  `base:financial-common`: shared form fields, dropdowns, reference attachment
  UI, and platform file pickers now belong to the financial-list feature. Asset
  and debt add actions resolve typed `SharedScreen` contracts. All create forms
  (cash, bank account, investment, insurance, building, land, liability, and
  recurring expense) submit through domain repositories and continue to the
  shared-asset screen after success. Decimal money fields use fixed-point
  parsing and reject malformed input.
- The legacy `base:financial-common` presentation module is no longer in the
  active Gradle graph. Its source remains in place as a rollback archive while
  the composition root registers only feature-owned typed create destinations.
- The architecture gate now ratchets this boundary with a dedicated
  `feature_to_compatibility_dependencies` metric, preventing the legacy
  presentation bundle from being added back to a feature.
- The financial-list module's unused DataStore, AtomicFu, and Coil Ktor
  network dependencies were removed after the local attachment picker moved
  into the feature; Android and iOS compilation still pass.
- The iOS app now uses the supported CocoaPods podspec integration: the
  generated ComposeApp pod is installed into `iosApp.xcworkspace`, the
  application no longer invokes a direct `embedAndSign` script, and a local
  iPhone 16 Pro simulator build/install/launch smoke test passes. The XCTest
  target stays a lightweight native workspace smoke test so it does not load
  the app's Firebase/Auth pods as test-bundle runtime dependencies; shared
  ComposeApp APIs remain covered by Kotlin/Native simulator tests.
- The checked-in CocoaPods support settings include the repository-level
  `nanopb` header path required by a clean Xcode derived-data build. A fresh
  simulator XCTest run now passes without relying on an older local cache.
- Kotlin/Native simulator test binaries now receive the complete transitive
  GoogleSignIn pod framework search/rpath set (`AppCheckCore`,
  `GoogleUtilities`, and Promises included), so ComposeApp/auth shared tests
  run without relying on an Xcode workspace xcconfig.
- Gradle configuration is now measured from Gradle's own profiled
  `Configuring Projects` phase rather than wrapper/JVM startup. The local
  five-run median is 4,133 ms, under the 5-second budget; the exporter still
  measures other build phases by wall-clock duration.

## Still pending for the 10/10 acceptance target

Runtime performance collection is intentionally deferred for this checkpoint
so the team can focus on a usable end-to-end application first. The pending
items below are measurement/CI hardening work, not a blocker for the Android
build and host-test checkpoint. The connected UI smoke test remains wired for
the configured runner; this workspace currently has no attached emulator.

- AndroidX Macrobenchmark is wired in an isolated self-instrumenting process,
  but runtime measurement is intentionally deferred while the usable app is
  prioritized. Cached dashboard content, startup, scrolling jank, iOS hitch
  time, retained-memory loops, framework growth, and the remaining build
  timings still need fixed-runner exports before the strict performance task
  can pass.
- The iOS workspace now has a shared XCTest target and an `xccov` exporter.
  Android Kover and iOS XCTest exports are wired into the self-hosted jobs.
  The simulator smoke test validates the native test host; the shared
  `ComposeApp` Kotlin/Native APIs are exercised by Gradle simulator tests.
  The framework is built outside Xcode's LLVM coverage instrumentation, so
  first-party iOS line coverage still measures 0.000%.
  The exporter excludes Firebase/Pods instead of presenting third-party
  coverage as application coverage; cross-platform coverage parity remains
  pending until Kotlin/Native coverage is instrumented.
- CI invokes the measured coverage gate in strict mode for every pull request.
  Full performance aggregation remains strict when manually requested with
  `run_performance`, but is intentionally not a pull-request prerequisite
  during the usability-first checkpoint.
- Production Android launch smoke test is wired to the app module and CI.
  When a configured emulator is available, the authenticated instrumentation
  smoke seeds a secure session and traverses login/recovery/registration,
  notification, dashboard, asset and liability forms, social, profile, and
  logout. The local workspace currently has no connected emulator, so this is
  kept as a runner check rather than reported as a local UI run.
- iOS startup now emits an `AppStartup` signpost that XCTest/Instruments can
  measure; the CI job builds the CocoaPods workspace, while simulator metric
  collection and export to the performance properties file remain runner work.
- Compatibility source archives under `functional/api` and `functional/data-*`
  are still retained for rollback/source compatibility, but active compile
  inputs now live under `base/*` and `data/*`. The archives can be physically
  deleted only after downstream consumers and external integrations are
  confirmed to be gone.
- Some extracted social and financial-list content/component files remain
  larger than the target 300-line presentation limit. Their route files are now
  small and independently wired, behavior is covered, and the remaining
  content-level extraction is maintainability work rather than a
  runtime-usability blocker.

The next acceptance step is to collect the remaining real five-run
Android/iOS runtime exports and enable Kotlin/Native coverage instrumentation.
The Android exporter and local source-level gates are strict and passing for
the migrated scope; they will expand automatically as each legacy vertical
crosses the same contracts.
