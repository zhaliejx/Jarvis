# GitHub Actions Workflows

This repository contains several GitHub Actions workflows to automate building, testing, and releasing the Jarvis Assistant Android application.

## Available Workflows

### 1. Build and Test (`android.yml`)
- Runs on every push to `main` and `develop` branches
- Runs on pull requests to `main`
- Builds the Android application
- Runs unit tests
- Creates debug and release APKs
- Uploads artifacts for download

### 2. Release APK (`release.yml`)
- Runs when a new tag is pushed (e.g., `v1.0.0`)
- Builds and signs a release APK
- Creates a GitHub release
- Uploads the signed APK to the release

### 3. Instrumented Tests (`instrumented-tests.yml`)
- Runs instrumented tests on multiple Android API levels
- Runs weekly on Sundays
- Tests on Android API levels 24, 28, 30, and 34
- Uploads test reports as artifacts

### 4. Code Quality (`code-quality.yml`)
- Runs code quality checks on every push and PR
- Includes ktlint for Kotlin code formatting
- Includes detekt for static code analysis
- Includes dependency vulnerability scanning

## Secrets Required

For the release workflow to work, you need to configure the following secrets in your GitHub repository:

- `SIGNING_KEY`: Base64-encoded keystore file
- `SIGNING_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias
- `KEY_PASSWORD`: Key password

## Usage

### Creating a Release
1. Update the version in `app/build.gradle`
2. Commit and push the changes
3. Create a new tag: `git tag v1.0.0`
4. Push the tag: `git push origin v1.0.0`
5. The release workflow will automatically create a GitHub release with the APK

### Monitoring Builds
- Check the "Actions" tab in your GitHub repository
- All workflows will show their status and logs
- Artifacts (APKs, reports) can be downloaded from completed workflows