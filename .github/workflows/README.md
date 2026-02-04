# GitHub Actions Workflows

This repository contains several GitHub Actions workflows to automate building, testing, and releasing the Jarvis Assistant Android application.

## Available Workflows

### 1. Build and Test (`android.yml`)
- Runs on every push to `main` and `develop` branches
- Runs on pull requests to `main`
- Builds the Android application
- Runs unit tests
- Creates debug APK and uploads as artifact
- Conditionally creates release APK (only for tagged releases)
- Runs lint checks in a separate job

**Artifacts Generated:**
- `debug-apk`: Debug APK for testing
- `release-apk`: Release APK (only on tag pushes)

### 2. Release APK (`release.yml`)
- Runs when a new tag is pushed (e.g., `v1.0.0`)
- Decodes the base64-encoded signing key
- Builds and signs a release APK
- Creates a GitHub release
- Uploads the signed APK to the release for download

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

For the release workflow to work, configure the following secrets in your GitHub repository (`Settings > Secrets and variables > Actions`):

- `SIGNING_KEY`: Base64-encoded keystore file
- `SIGNING_PASSWORD`: Keystore password
- `KEY_ALIAS`: Key alias (e.g., `jarvis`)
- `KEY_PASSWORD`: Key password

### Setting Up Signing Keys

1. **Generate a keystore** (if needed):
```bash
keytool -genkey -v -keystore release.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias jarvis
```

2. **Encode to base64**:
```bash
base64 -i release.keystore > release.keystore.b64
```

3. **Add secrets** from the base64-encoded content

4. **Secure the keystore** - Store locally, DO NOT commit to git

## Usage

### Building Debug APK
Every push triggers the build workflow. Download the `debug-apk` artifact from the workflow run.

### Creating a Release
1. Update the version in `app/build.gradle`
2. Commit and push the changes
3. Create a new tag: `git tag v1.0.0`
4. Push the tag: `git push origin v1.0.0`
5. The release workflow will automatically:
   - Build and sign the release APK
   - Create a GitHub Release
   - Upload the APK for download

### Downloading APKs

**Debug APK:**
- Go to the workflow run on GitHub
- Look for the "Artifacts" section
- Download `debug-apk`

**Release APK:**
- Go to the GitHub Release page
- Download the APK from the release assets

## Troubleshooting

### APK Download Not Working
- Verify the workflow completed successfully
- Check the "Artifacts" section in the workflow run
- For releases, ensure the GitHub Release was created

### Signing Errors
- Verify all signing secrets are configured correctly
- Ensure the keystore is valid and properly base64-encoded
- Check that key alias and passwords match your keystore

### Build Failures
- Check workflow logs for detailed error messages
- Common issues: missing SDK, gradle cache issues, dependency problems