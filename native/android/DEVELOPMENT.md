# FaceTec Plugin Development Guide

This guide explains how to set up and develop the Java version of the FaceTec plugin with proper IDE support, autocomplete, and testing capabilities.

## Prerequisites

- **Android Studio** (latest version recommended)
- **Java 8 or higher**
- **Android SDK** (API level 21+)
- **Gradle** (included with Android Studio)

## Development Environment Setup

### 1. Open in Android Studio

1. Launch Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to `native/android/` and select it
4. Wait for Gradle sync to complete

### 2. Project Structure

```
native/android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   └── java/mitra/cristianmarques/facetec/
│   │   │       └── FaceTecModule.java          # Main module
│   │   ├── test/                               # Unit tests
│   │   │   └── java/mitra/cristianmarques/facetec/
│   │   │       └── FaceTecModuleTest.java
│   │   └── androidTest/                        # Instrumented tests
│   │       └── java/mitra/cristianmarques/facetec/
│   │           └── FaceTecModuleInstrumentedTest.java
│   └── build.gradle
├── build.gradle                                 # Project-level build file
└── settings.gradle
```

## Development Features

### Autocomplete & IntelliSense

- **Java imports**: Android Studio provides full autocomplete for Java classes
- **React Native bridge**: Autocomplete for `ReactMethod`, `Promise`, etc.
- **Android framework**: Full autocomplete for Android APIs
- **Gradle dependencies**: Autocomplete for dependency management

### Code Navigation

- **Go to Definition**: Ctrl+Click (Cmd+Click on Mac) on any class/method
- **Find Usages**: Alt+F7 (Option+F7 on Mac)
- **Refactor**: Right-click → Refactor for safe code changes

## Testing

### Unit Tests (Robolectric)

Unit tests run without an emulator and provide fast feedback:

```bash
# Run from project root
cd native/android
./gradlew test

# Run specific test class
./gradlew test --tests FaceTecModuleTest

# Run with coverage
./gradlew testDebugUnitTestCoverage
```

**Test Location**: `app/src/test/java/`

### Instrumented Tests (Android)

These tests run on a real device or emulator:

```bash
# Run on connected device/emulator
./gradlew connectedAndroidTest

# Run specific test
./gradlew connectedAndroidTest --tests FaceTecModuleInstrumentedTest
```

**Test Location**: `app/src/androidTest/java/`

### Running Tests in Android Studio

1. **Unit Tests**: Right-click on test file → "Run"
2. **Instrumented Tests**: Right-click on test file → "Run"
3. **All Tests**: Right-click on test folder → "Run Tests"

## Development Workflow

### 1. Code Changes

1. Open `FaceTecModule.java` in Android Studio
2. Make your changes with full autocomplete support
3. Save the file

### 2. Testing

1. Write tests in the appropriate test directory
2. Run tests to verify functionality
3. Use test-driven development for new features

### 3. Building

```bash
# Build the library
./gradlew assembleDebug

# Build and install on device
./gradlew installDebug
```

## Adding New Methods

When adding new methods to `FaceTecModule.java`:

1. **Add the method**:
```java
@ReactMethod
public void newMethod(String param, Promise promise) {
    try {
        // Your implementation
        promise.resolve("Success");
    } catch (Exception e) {
        promise.reject("ERROR", e.getMessage());
    }
}
```

2. **Add tests**:
```java
@Test
public void testNewMethod() {
    faceTecModule.newMethod("test", mockPromise);
    verify(mockPromise).resolve("Success");
}
```

3. **Update JavaScript interface** in the main project

## Debugging

### Logging

```java
import android.util.Log;

@ReactMethod
public void debugMethod(Promise promise) {
    Log.d("FaceTecModule", "Debug message");
    // ... rest of method
}
```

### Breakpoints

1. Set breakpoints in Android Studio by clicking in the gutter
2. Run in debug mode (Run → Debug)
3. Use the debug console to inspect variables

## Common Issues & Solutions

### Gradle Sync Issues

```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### Test Failures

1. Check that all dependencies are properly imported
2. Verify test configuration in `build.gradle`
3. Ensure test packages match directory structure

### Autocomplete Not Working

1. File → Invalidate Caches / Restart
2. Check that the file is recognized as Java source
3. Verify project structure in Project view

## Best Practices

1. **Always write tests** for new functionality
2. **Use meaningful method names** that describe the action
3. **Handle errors gracefully** with proper Promise rejection
4. **Document complex methods** with JavaDoc comments
5. **Follow React Native bridge patterns** for consistency

## Resources

- [React Native Android Development](https://reactnative.dev/docs/android-setup)
- [Android Testing Guide](https://developer.android.com/training/testing)
- [Robolectric Documentation](http://robolectric.org/)
- [Mockito Documentation](https://site.mockito.org/)
