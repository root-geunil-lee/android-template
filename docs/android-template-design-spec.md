# Android Template Design Spec (2026)

## 1. 목적
- `ios-template`의 현재 기능, 데이터 계약, 유저 플로우를 Android 템플릿으로 **100% 동일하게 이식**한다.
- 본 문서는 Android 구현 시 UI 스타일, 상호작용, 아키텍처 경계를 고정하는 기준이다.

## 2. 소스 기준 (고정)
- 기준 레포: `ios-template`
- 기준 커밋 범위:
  - `a710a6d` Initialize ios-template repository
  - `3a981ce` Add native SwiftUI Hello World iOS template
  - `8c63b6f` Update porting specs to Supabase-integrated Flutter baseline
  - `9fd2cc1` feat: add iOS auth flow and Supabase Apple/OTP integration
  - `b00102f` Fix local StoreKit testing and billing sync gating
- 기준 문서:
  - `ios-template/docs/flutter-template-functional-spec.md`
  - `ios-template/docs/ios26-design-spec.md`
  - `ios-template/docs/android-template-porting-spec.md`

## 3. 패리티 원칙 (절대 규칙)
- 기능 패리티: iOS에 있는 기능은 Android에도 모두 존재해야 한다.
- 플로우 패리티: 진입 경로, 성공/실패 분기, 상태 전이는 iOS와 동일해야 한다.
- 데이터 패리티: 상품 ID, API path, payload field name을 변경하지 않는다.
- UX 패리티: 화면 정보 구조(IA)와 위험 액션 위치는 동일하게 유지한다.
- 플랫폼 적응: 내비게이션/다이얼로그/시트/햅틱/토스트는 Android 최신 패턴으로 구현하되, 의미/순서는 iOS와 동일해야 한다.

## 4. 개발 원칙 (고정)
- **TDD 기반 개발**: 모든 기능은 테스트를 먼저 작성한 뒤 구현한다.
  - 단위 테스트: 도메인 로직, 상태 전이, 유효성 검증
  - 통합 테스트: Supabase 인증 플로우, 결제 동기화 API 계약
  - UI 테스트: 핵심 유저플로우(로그인, OTP, 결제, 복원, 로그아웃)
- **최신 테크스택만 사용**: Android 2026 시점의 안정 최신(stable) 버전만 채택한다.
  - Kotlin 최신 안정 버전
  - Android Gradle Plugin 최신 안정 버전
  - Jetpack Compose BOM 최신 안정 버전
  - Navigation Compose 최신 안정 버전
  - Play Billing 최신 안정 버전
  - Coroutines/Flow 최신 안정 버전
  - 테스트 스택(JUnit5, kotest/MockK, Compose UI Test) 최신 안정 버전
  - 단, 프리뷰/알파/베타는 명시 승인 없이는 금지

## 5. 정보구조 (IA)
- 비인증 영역
  - Auth Method Selection
  - Email Sign In
  - OTP Verify
- 인증 영역
  - Home
  - My Page
  - Subscription
  - Purchase History
  - Edit Profile
  - Plan Selection
  - Payment Method
  - Transaction Detail
  - Paywall (sheet)

## 6. 유저 플로우 (iOS와 100% 동일)

### 5.1 소셜 로그인
1. 앱 실행 -> `Sign in`
2. Apple/Google/Kakao 선택
3. Supabase OAuth callback 복귀
4. 세션 저장
5. Home 이동
6. 옵션: billing status refresh

### 5.2 이메일 OTP 로그인
1. `Continue with Email`
2. 이메일 입력 후 `Send verification code`
3. OTP 6자리 검증 화면 이동
4. 6자리 입력 시 자동 검증(또는 Verify 버튼)
5. 성공 시 Home 이동

### 5.3 결제
1. Home에서 `Upgrade` 또는 로그인 화면의 `View plans`
2. Paywall 상품 선택
3. `Start subscription` / `Purchase` / `Remove ads`
4. 결제 성공 시 옵션으로 `/api/v1/billing/purchases/sync`
5. 결과 메시지 표시 후 시트 종료

### 5.4 복원
1. `Restore purchases`
2. 스토어 복원 API 호출
3. 보유 entitlement 수집
4. 옵션으로 backend sync
5. 결과 메시지 표시

### 5.5 로그아웃/탈퇴
1. My Page -> Danger Zone
2. 확인 다이얼로그
3. signOut
4. 인증 상태 리셋 후 인증 화면 복귀

## 7. 공통 디자인 시스템 (iOS/Android 공유)

### 6.1 컬러 시스템 (공통 고정)
- `color.background = #FFFFFF`
- `color.surface = #F5F5F5`
- `color.border = #E0E0E0`
- `color.text.primary = #111111`
- `color.text.secondary = #666666`
- `color.divider = #EEEEEE`
- `color.primary = #000000`
- `color.onPrimary = #FFFFFF`
- 브랜드 색상/그라디언트 미사용

### 6.2 타이포그래피 (공통 스케일)
- `display = 32`
- `title = 22`
- `body = 16`
- `caption = 13`
- 플랫폼 기본 폰트 사용:
  - iOS: SF Pro
  - Android: Roboto / Noto Sans 계열 시스템 폰트

### 6.3 코너 라운드 (공통)
- 기본 컨테이너 radius: `12`
- 주요 CTA/button radius: `16`
- OTP 박스 radius: `12~14`

### 6.4 스페이싱 (8pt grid 공통)
- `4, 8, 12, 16, 24, 32, 40`
- 화면 수평 패딩 기본값: `20`

### 6.5 Elevation (공통 룰)
- 기본 `0`
- 선택된 카드/강조 surface만 `1~2`
- heavy shadow 금지

### 6.6 아이콘 시스템 (공통 룰)
- Outline 스타일 우선
- 두께 얇은 단색 아이콘
- 의미 동일성 유지:
  - iOS SF Symbols <> Android Material Symbols 매핑

## 8. Android 2026 상호작용 규칙

### 7.1 Navigation
- Navigation Compose + type-safe route 사용
- Predictive Back 완전 지원
- Edge-to-edge 기본 적용
- 하단 탭은 시스템 Navigation Bar 패턴 준수
- 로그인 후 기본 랜딩은 Home (My Page 금지)

### 7.2 Dialog
- Material 3 AlertDialog 사용
- 위험 액션은 명확한 destructive copy 사용
- Logout: 1회 확인
- Delete Account: 2-step 확인
  - `DELETE` 입력 또는 체크박스 + 카운트다운

### 7.3 Sheet
- Modal Bottom Sheet 사용
- Partial/Expanded 상태 지원
- 드래그 핸들 표시
- 시트 하단 고정 CTA 영역 제공

### 7.4 Haptic
- 주요 액션 시 경량 햅틱
  - CTA tap, verify success, purchase success
- 실패 상태에는 진동 과다 사용 금지

### 7.5 Toast / Snackbar
- 기본 피드백은 Snackbar 사용
- 액션 없는 짧은 시스템 알림만 Toast 허용
- 에러는 가능한 인라인 메시지 우선

## 9. 기능 요구사항 (Android 구현 대상)

### 8.1 Auth
- Auth Method Selection
  - 초기 화면 입력 필드 금지
  - 버튼 순서(Android): Google -> Kakao -> Apple(지원 시)
  - `Continue with Email`
- Email Sign In
  - Email 입력, `Send verification code`
- OTP Verify
  - 6-digit 입력
  - resend cooldown
  - rate limit 표시
  - 자동 검증 + 수동 Verify

### 8.2 Billing / Payment
- Paywall 상품 4종
  - `monthly`, `annual`, `remove_ads`, `lifetime`
- 구독/일회성 분기 CTA
- 복원 버튼
- 구매/복원 결과 상태 표시
- `BILLING_SYNC_ENABLED`가 true일 때만 서버 sync

### 8.3 My Page
- Profile / Settings / Support / Danger Zone 섹션 유지
- Settings에는 정확히 아래 2개:
  - `Subscription`
  - `Purchase History`
- Danger Zone 최하단:
  - `Log out`
  - `Delete Account`

### 8.4 Subscription / Purchase History
- Subscription:
  - 현재 플랜 상태
  - Change Plan
  - Payment Method
  - Manage in Store
  - Cancel Subscription
- Purchase History:
  - Filter(All/Subscriptions/One-time)
  - Transaction List
  - Transaction Detail + Get receipt

## 10. API/데이터 계약 (iOS와 동일)

### 9.1 환경변수/설정 키
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_REDIRECT_URL`
- `API_BASE_URL`
- `BILLING_SYNC_ENABLED`
- `BILLING_BEARER_TOKEN`
- `BILLING_DEBUG_USER_ID`

### 9.2 엔드포인트
- `POST /auth/v1/otp`
- `POST /auth/v1/verify`
- `POST /auth/v1/logout`
- `GET /api/v1/billing/entitlements/me`
- `GET /api/v1/billing/purchases/me?limit=50`
- `POST /api/v1/billing/purchases/sync`

### 9.3 결제 동기화 payload field
- `platform`
- `storeProductId`
- `storeTransactionId`
- `externalOrderId`
- `status`
- `purchasedAt`
- `expiresAt`
- `canceledAt`
- `refundedAt`
- `raw`

## 11. Android 모듈 구조 권장안
- `app/`
- `core/design/`
- `core/navigation/`
- `core/network/`
- `features/auth/`
- `features/billing/`
- `features/main/`
- `features/mypage/`

## 12. 구현 완료 기준 (Definition of Done)
- [ ] iOS 플로우와 화면 전이 순서가 동일하다.
- [ ] 상품 ID 4종 및 sync payload가 iOS와 완전히 동일하다.
- [ ] 로그인 후 Home 랜딩이 보장된다.
- [ ] Logout/Delete가 Home에 노출되지 않고 Danger Zone에만 있다.
- [ ] Android Predictive Back 동작이 모든 스크린에서 자연스럽다.
- [ ] Dialog/Sheet/Haptic/Toast가 Android 2026 패턴을 따른다.
- [ ] 접근성(대비, 폰트 스케일, 터치 타겟 44dp+)이 통과된다.
- [ ] TDD 사이클(실패 테스트 -> 구현 -> 리팩터링) 로그가 PR에 기록된다.
- [ ] 신규 기능 PR마다 단위/통합/UI 테스트가 최소 1개 이상 추가된다.

## 13. 이 문서의 우선순위
- iOS 구현체와 상충 시: iOS 기능/플로우 패리티를 우선한다.
- Android 시각/상호작용 세부 구현은 본 문서 8장을 따른다.
