package uz.yalla.telemetry.sink

import uz.yalla.telemetry.event.AnalyticsEvent

/**
 * Destination for analytics events fanned out by `Telemetry`.
 *
 * Implementations must be non-blocking and may be called off the main thread.
 * They MUST NOT throw — `Telemetry` isolates failures defensively, but a sink that
 * throws still wastes the swallow and signals nothing. Implementations must also
 * not assume params are PII-screened; that is the caller's contract.
 */
public interface TelemetrySink {
    /** Records a single [event]. Must not block or throw. */
    public fun track(event: AnalyticsEvent)

    /**
     * Sets the current user, or clears it when [userId] is `null`. [userId] is an
     * opaque, non-PII pseudonym (never a phone/email/name). Must not block or throw.
     */
    public fun setUser(userId: String?)
}
