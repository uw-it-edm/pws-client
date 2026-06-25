# Application Modernization Report

## Iteration
Planning and baseline assessment (no production code changes yet).

## What changed

1. Added modernization plan document:
   - `application-modernization.md`
2. Captured and documented baseline build/tooling state:
   - Spring Boot `3.5.3`
   - Java `17`
   - Gradle wrapper `8.14`
   - Legacy JUnit 4/Vintage usage in `pws-client`
3. Added Spotless adoption as an explicit modernization work item based on requested quality gate.
4. Updated scope to exclude external `edm-e2e` integration testing from required completion gates.

## Why it changed

- Establish a reviewed, phased roadmap before dependency/platform upgrades.
- Reduce upgrade risk by sequencing platform, test-framework, and quality-tooling changes.
- Align work with requested constraints:
  - Preserve test intent
  - Avoid “test edits just to pass”
  - Validate via repeatable Gradle gates

## Validation results (current baseline)

- `./gradlew test` -> **PASS**
- `./gradlew spotlessCheck` -> **FAIL** (`spotlessCheck` task not currently defined)

## Implications

- The requested Spotless gate cannot pass until Spotless plugin/configuration is introduced.
- This is now a planned modernization deliverable, to be implemented before final completion criteria are claimed.

## Next execution checkpoint

1. Implement phase 1-2 modernization changes (tooling/platform baseline).
2. Add Spotless configuration.
3. Re-run and record:
   - `./gradlew test`
   - `./gradlew spotlessCheck`
   - startup + integration gates
