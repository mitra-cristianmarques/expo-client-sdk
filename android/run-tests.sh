#!/bin/bash

# FaceTec Plugin Test Runner
# This script provides easy commands to run different types of tests

set -e

echo "🧪 FaceTec Plugin Test Runner"
echo "================================"

case "$1" in
    "unit")
        echo "Running unit tests..."
        ./gradlew test
        ;;
    "instrumented")
        echo "Running instrumented tests..."
        ./gradlew connectedAndroidTest
        ;;
    "all")
        echo "Running all tests..."
        ./gradlew test
        ./gradlew connectedAndroidTest
        ;;
    "coverage")
        echo "Running tests with coverage..."
        ./gradlew testDebugUnitTestCoverage
        ;;
    "clean")
        echo "Cleaning project..."
        ./gradlew clean
        ;;
    "build")
        echo "Building project..."
        ./gradlew assembleDebug
        ;;
    "help"|"")
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  unit         - Run unit tests (fast, no emulator needed)"
        echo "  instrumented - Run instrumented tests (requires device/emulator)"
        echo "  all          - Run all tests"
        echo "  coverage     - Run tests with coverage report"
        echo "  clean        - Clean the project"
        echo "  build        - Build the project"
        echo "  help         - Show this help message"
        echo ""
        echo "Examples:"
        echo "  $0 unit           # Run unit tests"
        echo "  $0 instrumented   # Run instrumented tests"
        echo "  $0 all            # Run all tests"
        ;;
    *)
        echo "Unknown command: $1"
        echo "Use '$0 help' for available commands"
        exit 1
        ;;
esac

echo ""
echo "✅ Test run completed!"
