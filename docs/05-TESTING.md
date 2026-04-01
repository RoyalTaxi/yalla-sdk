# Testing Guide

> What to test, how to test it, and where tests live.

## Test Location

```
{module}/src/commonTest/kotlin/uz/yalla/{module}/
```

All tests are in `commonTest` — they run on all platforms (JVM + iOS simulator).

## Running Tests

```bash
# All tests in one module:
./gradlew :core:allTests

# All tests across entire SDK:
./gradlew allTests

# Quick compilation check (no execution):
./gradlew compileCommonMainKotlinMetadata
```

## What to Test

### Must Test
- **Domain models**: data class equality, factory methods, edge cases
- **Either extensions**: mapSuccess, mapFailure, onSuccess, onFailure
- **Preference keys**: uniqueness (already tested in PreferenceKeysTest)
- **Utility functions**: formatting, normalization, geo calculations
- **Component Colors/Dimens**: equality, defaults, copy behavior
- **Component Defaults**: factory function return values

### Should Test
- **API response parsing**: serialization/deserialization
- **State holders**: Saver round-trip, initial values
- **Map models**: LatLng validation, LatLngBounds builder, CameraPosition equality
- **Compression config**: presets, custom values

### Don't Test
- Platform-specific rendering (can't unit test UIKit/Android Views)
- Composable layout (use @Preview for visual verification)
- Ktor HTTP client internals (tested by Ktor)

## Test Patterns

### Data Class Equality
```kotlin
@Test
fun shouldImplementDataClassEquality() {
    val a = MyColors(container = Color.White, content = Color.Black)
    val b = MyColors(container = Color.White, content = Color.Black)
    assertEquals(a, b)
    assertEquals(a.hashCode(), b.hashCode())
}
```

### Factory Method Behavior
```kotlin
@Test
fun shouldFallbackToCashForUnknownPaymentType() {
    val result = PaymentKind.from("unknown")
    assertEquals(PaymentKind.Cash, result)
}
```

### Edge Cases
```kotlin
@Test
fun shouldHandleEmptyList() {
    val bounds = emptyList<GeoPoint>().toBoundingBox()
    // verify empty bounding box behavior
}
```

### Naming Convention
```
should{ExpectedBehavior}When{Condition}
shouldReturnCash → shouldReturnCashWhenIdIsUnknown
shouldBeValid → shouldBeValidForBoundaryValues
```

## Test Coverage by Module

| Module | Test Files | Status |
|--------|-----------|--------|
| core | 17 | Excellent |
| data | 7 | Good |
| design | 3 | Good |
| foundation | 5 | Good |
| primitives | 8 | Good |
| composites | 28 | Good |
| platform | 5 | Adequate |
| maps | 9 | Good (improved from 1) |
| media | 4+ | Improving |
| firebase | 3 | Adequate |
