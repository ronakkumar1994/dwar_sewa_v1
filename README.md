# DwarSewa (द्वार सेवा) — Android Application

DwarSewa is a full-featured on-demand home services platform built for Ranchi, Jharkhand. The Android application is implemented using modern **Kotlin** and **Jetpack Compose** following Material Design 3 (M3) guidelines, providing complete bilingual support (English and Hindi), role-based workflows (Customer, Provider, Admin), service booking, and promotional offers.

## Core Features

### 1. Bilingual Support (English & Hindi)
- Instant in-app language toggle (`EN` / `हिं`) across all screens.
- Comprehensive localization of all service titles, package names, statuses, and navigation labels.

### 2. Multi-Role Architecture
- **Customer**:
  - Locality-based service browsing (Lalpur, Kanke Road, Morabadi, Harmu, Doranda, Bariatu, and Other).
  - Categorized services: AC Repair (❄️), Home Cleaning (🧹), Salon at Home (💇), Plumber (🔧), Electrician (💡).
  - Service detail views with package durations and transparent pricing (₹).
  - Interactive booking modal with time slot selection, Ranchi address input, payment method (Cash / Online), and notes.
  - Active booking status tracker with real-time progress (`Pending`, `Assigned`, `Accepted`, `On the way`, `Started`, `Completed`).
  - Auto-scrolling promotional offers carousel with discount codes and terms.
  - Customer account hub with quick actions (My Bookings, My Devices, Help & Support) and menu pages (Addresses, Ratings, Referrals, Settings, About).
- **Service Provider**:
  - Live Online/Offline toggle with administrative verification check.
  - Verification badge and warning banners (`Pending verification` / `Blocked`).
  - Jobs tab with today's jobs count, today's earnings, and one-click actions to advance job lifecycle status.
  - Earnings overview and job completion statistics.
  - Partner profile with registered service areas, experience years, and qualifications.
- **Administrator**:
  - Real-time operations overview: Bookings today, Auto-assigned, Unassigned, Revenue.
  - Partner KYC management: Approve, Reject, or Block provider accounts.
  - Offer manager: Publish new promotional deals with custom codes, dates, and bilingual descriptions.

### 3. Authentication & Test Accounts
- 10-digit mobile number input with OTP validation.
- One-click preview test accounts for rapid testing:
  - **Customer Preview**: `9999999999` (Test OTP: `123456`)
  - **Provider Preview**: `9999999998` (Test OTP: `123456`)
- Direct Admin login shortcut for platform oversight.

## Technology Stack

- **Framework**: Jetpack Compose (Material 3)
- **Language**: Kotlin 2.0+
- **Architecture**: MVVM with reactive StateFlow repository (`DwarSewaRepository`)
- **Design System**: Material Design 3 with custom brand colors (Terracotta Orange `#E05A2B` & Deep Teal `#1E4D5E`)
- **Target SDK**: Android SDK 35 / 36

## Building & Running

Open the project in Android Studio (Ladybug or newer) or compile using Gradle:

```bash
gradle assembleDebug
```

The debug APK will be generated at `app/build/outputs/apk/debug/app-debug.apk`.
