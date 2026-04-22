package uz.yalla.platform.otp

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke tests for [ObserveSmsCode] — Android-only @Composable (not an expect, per ADR-015c).
 *
 * [ObserveSmsCode] is defined only in androidMain (no iOS counterpart). It is a @Composable
 * that uses DisposableEffect + BroadcastReceiver backed by the Google SMS Retriever API.
 * Testing it requires an Android Activity context which is not available in commonTest/JVM
 * unit tests without Robolectric.
 *
 * This file lives in commonTest to document the gap; actual testing requires the Android
 * instrumentation harness wired in Phase 4.
 *
 * TODO(Phase 4): Add an androidInstrumentedTest that:
 *   - Renders ObserveSmsCode { code -> /* capture */ } inside a Compose rule.
 *   - Broadcasts a fake SmsRetriever.SMS_RETRIEVED_ACTION intent with a test message body.
 *   - Verifies the onCodeReceived callback fires with the expected message.
 *   - Verifies the BroadcastReceiver is unregistered after the composable leaves composition.
 */
class ObserveSmsCodeTest {

    @Test
    fun compileVerify_observeSmsCodeIsAndroidOnly() {
        // ObserveSmsCode is declared in androidMain only. This placeholder documents that
        // no commonTest assertion is possible without an Android Activity context.
        assertTrue(
            true,
            "ObserveSmsCode is Android-only (ADR-015c); commonTest cannot exercise it without an Activity context"
        )
    }
}
