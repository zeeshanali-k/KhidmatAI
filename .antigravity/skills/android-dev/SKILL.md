---
name: compose-multiplatform-rules
description: Enforce Kotlin Multiplatform + Compose architectural conventions, module boundaries, and coding standards for Android/iOS shared projects. Use when creating or refactoring KMP features, reorganizing project structure, adding platform-specific code, setting up DI/state/navigation, or reviewing Compose Multiplatform code quality.
---

# Compose Multiplatform Rules

## Overview

Apply consistent project structure and implementation rules for this repository's KMP layout (`composeApp`, `composeApp:androidApp`, `iosApp`).
Prefer common-first implementation and isolate platform-specific APIs behind interfaces or `expect/actual` boundaries.

## Workflow

1. Inspect current module and source-set layout before edits.
2. Choose the smallest valid target scope:
- `commonMain` for shared business logic and Compose UI
- `androidMain` for Android-only integrations
- `iosMain` for iOS-only integrations
3. Follow architecture and UI/state rules from the references.
4. Validate by running the narrowest Gradle task that exercises the modified module.

## Repository Map

- Shared KMP module: `composeApp`
- Android entry module: `composeApp/androidApp`
- iOS host app: `iosApp`
- Version catalog: `gradle/libs.versions.toml`

## Use References By Need

- Structure and module/layer boundaries: `references/architecture-rules.md`
- Compose UI, theming, and adaptive layout: `references/compose-rules.md`
- ViewModel and MVI contracts: `references/mvi-rules.md`
- Dependency injection with Koin: `references/di-koin-rules.md`
- KMP platform boundaries and `expect/actual` usage: `references/cmp-rules.md`
- Networking and API error mapping: `references/networking-ktor-rules.md`
- Local persistence with Room: `references/database-room-rules.md`
- Build, Gradle, version catalog, and KSP: `references/build-gradle-rules.md`
- Testing strategy and mandatory coverage: `references/testing-rules.md`
- Security baselines and release hardening: `references/security-rules.md`
- Naming, conventions, and Compose performance: `references/code-style-rules.md`

Load only the relevant reference file(s) for the task.

## Mandatory Rules

- Keep UI and business logic separate; place domain/use-case logic outside composables.
- Keep `commonMain` free of platform-only APIs.
- Introduce `expect/actual` only for true platform differences.
- Define dependencies in `gradle/libs.versions.toml` before wiring module build scripts.
- Avoid duplicate dependencies and keep source-set dependencies minimal.
- Add or update tests when behavior changes.

## Implementation Checklist

- Target source set is correct (`commonMain` vs `androidMain` vs `iosMain`).
- Package names and folder structure match conventions.
- State is hoisted; composables remain preview-friendly and side-effect aware.
- DI entry points are in platform/app layers; shared logic depends on abstractions.
- Build scripts and dependencies remain deterministic and version-catalog based.
- Verification command(s) run successfully or failure reason is reported.
