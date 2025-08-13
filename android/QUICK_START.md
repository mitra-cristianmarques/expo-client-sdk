# FaceTec Plugin - Quick Start Guide

## 🚀 Getting Started

### 1. Open in Android Studio
```bash
# Navigate to the Android project
cd native/android

# Open in Android Studio
# File -> Open -> Select the 'android' folder
```

### 2. Project Structure
```
native/android/
├── src/
│   ├── main/java/mitra/biometricsdk/facetec/
│   │   └── FaceTecModule.java          # Main module with React Native bridge
│   ├── test/java/mitra/biometricsdk/facetec/
│   │   └── FaceTecModuleTest.java      # Unit tests
│   └── androidTest/java/mitra/biometricsdk/facetec/
│       └── FaceTecModuleInstrumentedTest.java  # Instrumented tests
├── build.gradle                         # Project configuration
├── app/build.gradle                     # App configuration
├── run-tests.sh                         # Test runner script
└── DEVELOPMENT.md                       # Detailed development guide
```

## 🧪 Running Tests

### Quick Test Commands
```bash
# Run unit tests (fast, no emulator needed)
./run-tests.sh unit

# Run instrumented tests (requires device/emulator)
./run-tests.sh instrumented

# Run all tests
./run-tests.sh all

# Run with coverage
./run-tests.sh coverage

# Clean and rebuild
./run-tests.sh clean
./run-tests.sh build
```

### Manual Test Commands
```bash
# Unit tests only
./gradlew test

# Build the project
./gradlew build

# Clean project
./gradlew clean
```

## ✨ Development Features

### Autocomplete & IntelliSense
- **Full Java support** in Android Studio
- **React Native bridge** autocomplete
- **Android framework** APIs
- **Gradle dependency** management

### Code Navigation
- **Go to Definition**: Ctrl+Click (Cmd+Click on Mac)
- **Find Usages**: Alt+F7 (Option+F7 on Mac)
- **Refactor**: Right-click → Refactor

## 🔧 Adding New Methods

### 1. Add Method to FaceTecModule.java
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

### 2. Add Tests
```java
@Test
public void testNewMethod() {
    faceTecModule.newMethod("test", mockPromise);
    verify(mockPromise).resolve("Success");
}
```

### 3. Build & Test
```bash
./gradlew build
./gradlew test
```

## 📱 Current Methods Available

- `initializeSDK(Promise)` - Initialize FaceTec SDK
- `startVerification(ReadableMap, Promise)` - Start verification session
- `getSDKVersion(Promise)` - Get SDK version
- `isDeviceSupported(Promise)` - Check device compatibility

## 🐛 Debugging

### Logging
```java
import android.util.Log;

Log.d("FaceTecModule", "Debug message");
```

### Breakpoints
1. Set breakpoints in Android Studio
2. Run in debug mode (Run → Debug)
3. Use debug console to inspect variables

## 📚 Next Steps

1. **Read DEVELOPMENT.md** for comprehensive development guide
2. **Integrate actual FaceTec SDK** in the placeholder methods
3. **Add more comprehensive tests** for new functionality
4. **Test on real devices** using instrumented tests

## 🆘 Troubleshooting

### Common Issues
- **Gradle sync fails**: Run `./gradlew clean` then `./gradlew build`
- **Tests fail**: Check that all dependencies are properly imported
- **Autocomplete not working**: File → Invalidate Caches / Restart

### Getting Help
- Check the `DEVELOPMENT.md` file for detailed information
- Review test output for specific error messages
- Ensure Android Studio is properly configured for Java development
