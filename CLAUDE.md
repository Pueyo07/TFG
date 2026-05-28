# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run a single test class
./gradlew test --tests "com.example.tfgaplicacion.ExampleUnitTest"

# Clean build
./gradlew clean

# Lint check
./gradlew lint
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

## Project Overview

Single-module Android app (TFGAplicacion) that helps users track and overcome addictions. It uses a **gamification system** inspired by League of Legends ranks to motivate progress.

- **namespace**: `com.example.tfgaplicacion`
- **minSdk**: 24 (Android 7.0), **compileSdk/targetSdk**: 36
- **Language**: Kotlin, JVM target Java 11
- ViewBinding and DataBinding both enabled

## Architecture

**MVVM / Repository pattern** without a dedicated Application class. No Hilt/Dagger — dependencies are manually passed.

```
ui/          Activities, Fragments, Adapters (presentation layer)
data/        DAOs, Repositories, AppDatabase (data layer)
model/       Room entities and plain data classes
```

### Navigation

Activity-based, no Jetpack Navigation component:

- `MainActivity` (launcher) — lists all addictions for the current user; shows total points, rank, and badges
- `AdiccionDetailActivity` — receives `adiccion_id` via Intent extra; hosts two fragments via `TabLayout`:
  - `CalendarioFragment` — date picker to log daily completion or relapse + anxiety slider
  - `EstadisticasFragment` — streak/success stats, goal config, delete/reset addiction
- `LeyendaActivity` — static rank reference screen

### Data Layer

Room database (`adicciones_database`, version 3, `fallbackToDestructiveMigration` enabled):

| Entity | Table | Key relations |
|--------|-------|---------------|
| `Usuario` | `usuarios` | root entity |
| `Adiccion` | `adicciones` | FK → Usuario (CASCADE delete) |
| `RegistroDiario` | `registros_diarios` | FK → Adiccion (CASCADE delete) |
| `ProgresoUsuario` | `progreso_usuario` | single-row progress store |
| `Insignia` | `insignia` | achievement definitions |

Repositories (`AdiccionRepository`, `ProgresoRepository`, `UsuarioRepository`) expose suspend functions and Flow for reactive UI updates. DAOs use Kotlin coroutine extensions.

### Gamification / Domain Logic

- **Points**: 10 pts per completed day recorded in `RegistroDiario`
- **Ranks**: `RangoLoL` — 27 tiers from Iron IV to Challenger I; rank is computed from `puntosTotales` in `ProgresoUsuario`
- **Badges (`Insignia`)**: 20+ achievements seeded at DB creation; categories: starting, days (1/7/30/100/365 streaks), goals, ranks, special
- **Addiction types**: 8 predefined (`InfoAdiccion` data class with benefits/strategies/quotes) + custom; category drives automatic color theming (sustancias=purple, digital=blue, comportamental=green)

### Key Implementation Patterns

- Coroutines launched with `lifecycleScope.launch`; flows collected with `collectLatest` inside `repeatOnLifecycle`
- `AppDatabase` is a singleton (companion object with `@Volatile` instance)
- `AdiccionAdapter` uses `ListAdapter` + `DiffUtil.ItemCallback`
- Colors applied programmatically via `GradientDrawable`; date arithmetic uses `Calendar` API with day-level normalization
