package uz.yalla.foundation.location

// LocationManager tests live in iosTest/kotlin/uz/yalla/foundation/location/LocationManagerTrackingTest.kt.
//
// Rationale: dev.icerock.moko.geo.LocationTracker is an `expect class` whose platform actuals
// require native constructors (PermissionsController + CLLocationManager / FusedLocationProviderClient).
// On iOS the PermissionsController is a typealias for PermissionsControllerProtocol (an interface)
// so a fake can be constructed in iosTest. On Android it requires a ComponentActivity — not feasible
// in a JVM unit test without instrumentation, and the AGP KMP plugin (com.android.kotlin.multiplatform.library)
// does not expose a JVM unit-test task (only androidDeviceCheck).
//
// All five requested scenarios — idempotent startTracking, stopTracking-before-start no-op,
// scope cancellation, updatePermissionState propagation, and getCurrentLocationOrDefault default —
// are covered in iosTest, which is compiled into both iosArm64 and iosSimulatorArm64 and is
// executed by ./gradlew :foundation:allTests via iosSimulatorArm64Test.
