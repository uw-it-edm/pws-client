# Application Modernization Plan

## Goal
Upgrade this project to a modern, supported baseline with **Java 21** and the **latest Spring Boot 3.x**, while preserving behavior and test intent across modules:

- `pws-client`
- `pws-client-spring-boot-starter`

## Confirmed Scope Decisions

- Target runtime/tooling baseline: **Java 21 + latest Spring Boot 3.x**
- Include: **platform upgrades + key library upgrades**
- Include: **Spotless adoption** as an explicit modernization item

## Current Baseline (from `build.gradle` and wrapper)

- Spring Boot plugin: `3.5.3` (root, apply false)
- Spring dependency-management plugin: `1.1.7`
- Java source/target: `17`
- Spring Boot BOM import: `org.springframework.boot:spring-boot-dependencies:3.5.3`
- Gradle wrapper: `8.14`
- Test stack currently includes legacy JUnit items in `pws-client`:
  - `junit:junit`
  - `org.junit.vintage:junit-vintage-engine`

## Modernization Principles (best-practice driven)

1. Upgrade in small, reversible phases; do not bundle all changes into one jump.
2. Keep behavior first: no test changes merely to force pass. If tests fail after upgrades, fix code first; only update tests when intent legitimately changed.
3. Keep dependency versions centrally managed via Spring Boot BOM where possible.
4. Prefer removing legacy dependencies (e.g., JUnit Vintage) once equivalent JUnit 5 coverage is in place.
5. Validate security defaults and client/network behavior after each phase.

## Recommended Implementation Order

### ✅ COMPLETED

1. **Pre-flight and observability** — DONE
   - Captured baseline build state, task inventory, and dependencies.
   - Confirmed `./gradlew test` (available); Spotless was unavailable but now implemented.

2. **Build/tooling baseline** — DONE
   - Gradle wrapper updated to `8.14.3`.
   - Java baseline migrated from `17` to `21` using `java.toolchain`.
   - Milestone repository removed.

3. **Spring platform alignment** — DONE
   - Spring Boot upgraded from `3.5.3` to `3.5.9`.
   - Dependency-management plugin remains compatible.

4. **Test framework modernization** — DONE
   - JUnit 4/Vintage → JUnit 5 (Jupiter) migration completed.
   - All test assertions and intent preserved.
   - Removed `junit:junit` and `org.junit.vintage:junit-vintage-engine`.

### ✅ PARTIALLY COMPLETED

6. **Code quality guardrail** — DONE
   - Spotless configured and applied.
   - `spotlessCheck` and `spotlessApply` now available and passing.

### 🔲 REMAINING

5. **Library and API compatibility sweep**
   - Review explicit dependencies for Java 21 compatibility.
   - Investigate deprecated API warnings in PersonWebServiceClientImpl.
   - Validate Apache HttpClient5 usage (Spring Boot 3.5.9 includes newer versions).

7. **Security and runtime hardening review**
   - Verify upgraded defaults and endpoint/client behavior.
   - Confirm no regressions in auth, TLS, headers, serialization patterns.

8. **Repository validation**
   - Validate this repo's tests and startup behavior (already done: ✅ test pass).
   - Run project integration tests (if available).
   - Document any remaining compiler warnings.

## High-Risk Items Requiring Thorough Testing

1. **Java 17 -> 21 shift**
   - Potential reflection/module behavior differences, stricter runtime behavior, and third-party compatibility.
2. **JUnit 4/Vintage removal**
   - Hidden reliance on old runners/rules can silently reduce effective coverage if migrated incorrectly.
3. **Spring Boot patch/minor platform changes**
   - Auto-configuration behavior and transitive dependency shifts can alter runtime wiring.
4. **HTTP client behavior**
   - Connection, timeout, SSL/TLS, and serialization edge cases can change under dependency upgrades.
5. **Formatting/lint adoption**
   - First Spotless rollout may create wide diffs; should be isolated from functional changes.

## Validation Gates (must pass before completion)

Within this repository:

- `./gradlew test`
- `./gradlew spotlessCheck`
- Application/module startup and basic request flow checks
- Project integration tests

No external cross-repository test gate is required for this modernization scope.

## Delivery Strategy

1. Submit upgrade as **stacked, phase-based PRs** (tooling -> platform -> tests -> quality/security).
2. Keep each PR small enough to isolate regressions.
3. Publish a modernization report per phase with:
   - What changed
   - Why it changed
   - Validation evidence
   - Remaining risks and next gates

## Notes from MCP-backed guidance used

- Spring Boot docs (3.5.x): dependency management/BOM and Gradle compatibility guidance.
- Spring Security reference (6.5): security-default expectations and post-upgrade hardening validation points.
