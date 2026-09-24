# pws-client
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f132e997a97044d8a6011c29edfe459c)](https://app.codacy.com/app/uw-it-edm/pws-client?utm_source=github.com&utm_medium=referral&utm_content=uw-it-edm/pws-client&utm_campaign=Badge_Grade_Dashboard)

## Build and release versioning

This project does not hardcode a release version in `build.gradle`.

- Local builds default to `0.0.0-SNAPSHOT`.
- The publish workflow sets `releaseVersion` from the Git tag name.

Examples:

- Tag `1.0.1` publishes version `1.0.1`
- Tag `2.0.0-rc1` publishes version `2.0.0-rc1`

## Local commands

Build and test with snapshot version:

```bash
./gradlew clean build
```

Simulate a release version locally:

```bash
./gradlew clean publishToMavenLocal -PreleaseVersion=2.0.0
```

## GitHub Actions workflows

This repository uses separate workflows for CI and publishing:

- `.github/workflows/ci.yml`: runs build/test on all branch pushes and pull requests.
- `.github/workflows/publish.yml`: publishes to GitHub Packages on tag pushes.

Publish command used by CI:

```bash
./gradlew publish -PreleaseVersion="${GITHUB_REF_NAME}"
```
