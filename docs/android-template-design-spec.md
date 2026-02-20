# Android Template Design Spec (2026)

## 1. Purpose
- This project MUST port `ios-template` with 100% parity for feature, flow, data contract, and IA.
- This project MUST adapt interaction patterns to Android-native 2026 quality.

## 2. Hard Constraints (Immutable)
- Product IDs MUST remain exactly:
  - `monthly`
  - `annual`
  - `remove_ads`
  - `lifetime`
- API endpoints and payload fields MUST NOT be renamed.
- Entry points, success/failure branches, and state transitions MUST match iOS.
- IA placement MUST remain identical:
  - My Page
  - Subscription
  - Purchase History
  - Danger Zone
- Visual design tokens MUST remain shared across iOS and Android:
  - color system
  - typography scale
  - spacing grid
  - corner radius
  - elevation policy
  - icon style

## 3. Shared Visual Design Tokens

### 3.1 Color (Shared)
- `color.background = #FFFFFF`
- `color.surface = #F5F5F5`
- `color.border = #E0E0E0`
- `color.text.primary = #111111`
- `color.text.secondary = #666666`
- `color.divider = #EEEEEE`
- `color.primary = #000000`
- `color.onPrimary = #FFFFFF`

### 3.2 Typography (Shared Scale)
- `display = 32`
- `title = 22`
- `body = 16`
- `caption = 13`

### 3.3 Shape, Spacing, Elevation, Icon
- Corner radius MUST use:
  - default container `12dp`
  - primary CTA `16dp`
  - OTP box `12dp~14dp`
- Spacing MUST use 8pt grid:
  - `4, 8, 12, 16, 24, 32, 40`
- Base horizontal content padding MUST be `20dp`.
- Elevation MUST be minimal:
  - default `0`
  - emphasized/selectable surfaces `1~2`
- Icons MUST be monochrome outline style.

## 4. Architecture and State Management Contract

### 4.1 App Architecture
- App MUST be Single-Activity.
- UI MUST be Jetpack Compose.
- DI MUST use Hilt.
- Async/state MUST use Coroutines + Flow.

### 4.2 Module Layout (Pseudo Diagram)
```text
app/
  MainActivity (single activity, edge-to-edge host)
  AppNavHost
core/
  design/
  navigation/
  network/
  storage/
  ui/
features/
  auth/
  billing/
  main/
  mypage/
```

### 4.3 UI State and Event Contract
- Screen state MUST use `UiState` sealed class:
  - `Idle`
  - `Loading`
  - `Success<T>`
  - `Error(message)`
- One-off actions MUST use `UiEvent` stream:
  - `ShowSnackbar(message)`
  - `Navigate(route)`
  - `OpenSheet(id)`
  - `DismissSheet(id)`

### 4.4 Repository and Mapping Rules
- Repository layer MUST be the single source of business truth.
- DTO -> Domain mapping MUST happen in repository/data layer, not Composable.
- Network errors MUST be normalized to domain errors before UI.
- Caching policy:
  - Auth/session: persistent (encrypted)
  - Product catalog: in-memory cache during app session
  - Entitlements: memory + short-lived persisted snapshot MAY be used
  - Cache MUST NOT violate iOS-equivalent state transitions

### 4.5 Secure Storage Policy
- Session tokens MUST be stored in encrypted storage:
  - `EncryptedSharedPreferences` or equivalent encrypted storage
  - DataStore for structured session metadata
- Access token, refresh token, user id MUST be cleared on logout/delete account.

## 5. Insets and Edge-to-Edge Contract

### 5.1 Mandatory Edge-to-Edge
- Activity MUST enable edge-to-edge.
- Every screen MUST consume/apply insets for:
  - status bar
  - navigation bar
  - IME

### 5.2 Insets Application Rules
- Top app bars MUST apply status bar inset.
- Scroll/content containers MUST apply navigation bar and IME insets.
- Bottom sticky CTA (especially paywall sheet) MUST stay above nav bar inset.

### 5.3 Padding Rule with Shared Tokens
- Effective horizontal padding MUST be:
  - `max(20dp, safeDrawingInsetLeft/Right)`
- Shared token `20dp` is baseline; insets MAY increase but MUST NOT reduce below `20dp`.

## 6. Navigation and Predictive Back Contract

### 6.1 Graph Split
- Navigation MUST separate:
  - unauth graph
  - auth graph

### 6.2 Navigation Graph (Pseudo Diagram)
```text
root
├─ unauth
│  ├─ auth/methods
│  ├─ auth/email
│  └─ auth/otp?email=
└─ auth
   ├─ home
   ├─ mypage
   ├─ mypage/edit-profile
   ├─ mypage/subscription
   ├─ mypage/purchase-history
   ├─ mypage/transaction/{id}
   └─ paywall (modal sheet)
```

### 6.3 Predictive Back Priority
- Back handling order MUST be:
  1. dismiss sheet
  2. dismiss dialog
  3. pop nav back stack
  4. exit app

### 6.4 Paywall Back Rules
- If paywall is open, back MUST dismiss paywall first.
- Dismissing paywall MUST emit `.cancelled` result path equivalent to iOS.

### 6.5 Deep Link Entry Routing
- OAuth redirect deep link MUST route into auth graph callback handler first.
- Callback completion MUST resolve to iOS-equivalent destination:
  - success -> Home
  - failure -> Auth screen with error feedback

## 7. Material 3 Component Mapping (iOS -> Android)

| iOS Pattern | Android 2026 M3 Mapping | Required Rule |
|---|---|---|
| NavigationStack | Navigation Compose NavHost | Separate unauth/auth graph |
| TabView | NavigationBar + NavHost | Home default after login |
| confirmationDialog | AlertDialog | Danger actions require explicit confirmation |
| Sheet paywall | ModalBottomSheet | Sticky CTA above nav bar insets |
| List+Section | LazyColumn + grouped section headers | Keep IA grouping identical |
| Toolbar title/actions | TopAppBar | Edge-to-edge insets applied |
| Inline helper/error text | Supporting text + inline error style | Inline first for errors |
| Toast-like passive message | Snackbar/Toast fallback | Snackbar first, Toast passive only |

### 7.1 OTP Component Rules
- OTP input MUST support 6 digits only.
- Input MUST support:
  - auto-focus
  - paste
  - backspace handling
  - numeric keyboard
- Auto-verify MUST trigger when 6 digits entered.
- Manual `Verify` button MUST exist as fallback.
- Accessibility:
  - each digit slot MUST expose positional label (digit x of 6)
  - touch target MUST be at least `48dp`

## 8. Billing Contract (Google Play Billing, Latest Stable)

### 8.1 Product Mapping (App Logic IDs Unchanged)
- App logic IDs MUST stay unchanged:
  - `monthly`, `annual`, `remove_ads`, `lifetime`
- Play Billing mapping SHOULD be:
  - `monthly` -> SUBS product/base plan for monthly renewal
  - `annual` -> SUBS product/base plan for annual renewal
  - `remove_ads` -> INAPP non-consumable
  - `lifetime` -> INAPP non-consumable
- Offer tokens MAY vary by market/experiment, but app logic IDs MUST NOT change.

### 8.2 Purchase Lifecycle
- Query:
  - MUST query product details for SUBS and INAPP separately.
- Launch:
  - MUST launch billing flow with selected product/offer token.
- Update handling:
  - MUST handle `PURCHASED`, `PENDING`, `USER_CANCELED`, and error codes.
- Acknowledge:
  - MUST acknowledge eligible PURCHASED transactions.
- Restore:
  - MUST query existing purchases for SUBS + INAPP.
- Entitlement calculation:
  - MUST map active subscription and non-consumable ownership to iOS-equivalent entitlements.

### 8.3 Sync Payload Rules (Android)
- Endpoint MUST remain:
  - `POST /api/v1/billing/purchases/sync`
- Field mapping MUST be:
  - `platform = "android"`
  - `storeProductId = productId`
  - `storeTransactionId = purchaseToken`
  - `externalOrderId = orderId or null`
  - `status` mapped from purchase state (`active`, `pending`, `canceled`, `failed`, `refunded`, `expired`)
  - `raw` MUST include diagnostic store fields, for example:
    - `purchaseToken`
    - `orderId`
    - `packageName`
    - `purchaseTime`
    - `purchaseState`
    - `acknowledged`
    - `autoRenewing`
    - `originalJson`
    - `signature`

## 9. OAuth and Deep Links Contract (Supabase)

### 9.1 OAuth Launch
- OAuth MUST use Chrome Custom Tabs.
- Redirect MUST use App Links/intent-filter compatible callback.

### 9.2 Redirect URL and Manifest
- `SUPABASE_REDIRECT_URL` MUST map to Android manifest intent-filter:
  - scheme, host, and path MUST match exactly.
- Callback route handler MUST parse auth parameters and hand off to auth domain.

### 9.3 Session Persistence and Clear Rules
- On auth success:
  - MUST persist session securely.
- On logout:
  - MUST clear token/session storage and volatile caches.
- On delete account:
  - MUST clear local session exactly as logout, then return to unauth root.

## 10. Accessibility and Quality Baseline
- Touch targets MUST be >= `48dp`.
- TalkBack labels/roles MUST be provided for interactive controls.
- Dynamic font scaling MUST be supported without clipping critical actions.
- Error handling MUST follow:
  1. inline message first
  2. Snackbar second
  3. Toast only for passive notices

## 11. Testing Policy (TDD Mandatory)

### 11.1 Per-PR Minimum Tests
- Each feature PR MUST include at least:
  1. one unit test (state or business logic)
  2. one integration test (repository + MockWebServer)
  3. one UI test (Compose)

### 11.2 TDD Flow
- Implementation MUST follow:
  1. write failing test
  2. implement minimal passing code
  3. refactor while keeping tests green

## 12. PR Checklist
- [ ] iOS parity preserved for feature, flow, and state transitions
- [ ] Product IDs unchanged (`monthly`, `annual`, `remove_ads`, `lifetime`)
- [ ] Endpoints/payload fields unchanged
- [ ] IA placement unchanged (My Page/Subscription/Purchase History/Danger Zone)
- [ ] Edge-to-edge and insets rules applied on all affected screens
- [ ] Predictive back order implemented (sheet > dialog > pop > exit)
- [ ] Dialog/sheet behavior matches contract
- [ ] A11y baseline passed (48dp, TalkBack, font scaling)
- [ ] Per-PR test minimum met (unit + integration + UI)

## 13. Non-goals
- Redesigning iOS information architecture.
- Renaming product IDs or API schema.
- Introducing domain-specific copy tied to one app vertical only.

## 14. Forbidden Patterns
- Legacy Fragment-first navigation for new screens.
- Ignoring insets in edge-to-edge layouts.
- Hardcoded Toast for actionable errors.
- Business logic inside Composable functions.
- Storing tokens in plain SharedPreferences.
- Changing ID/endpoint/payload names to fit local implementation.

