package uz.yalla.foundation.infra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

/**
 * Base class for every product ViewModel. Provides the shared loading-indicator state ([loading])
 * and a crash-safe coroutine scope ([safeScope]) for fire-and-forget UI work.
 *
 * The crash-free contract is split deliberately: Orbit intent flow is guarded by the container's
 * `crashGuard()`, while non-Orbit work launched on [safeScope] is the explicit log-and-swallow
 * escape hatch documented below.
 */
public abstract class BaseViewModel : ViewModel() {
    private val loadingController = LoadingController(viewModelScope)

    /**
     * Anti-flicker loading flag: `true` only once in-flight [launchWithLoading]/[LoadingController]
     * work outlives the grace period, and held visible for at least the minimum-visible window.
     */
    public val loading: StateFlow<Boolean> = loadingController.loading

    private val handler =
        CoroutineExceptionHandler { _, throwable ->
            // Log only the exception type, never the throwable's message or stack trace. safeScope
            // backs the most sensitive flows (login/OTP, add-card, profile), and there is no
            // release-aware Kermit sink in the SDK, so a full throwable would write OTPs / phone
            // numbers / tokens carried in its message to Logcat / os_log in production (CWE-532).
            // Mirrors the network layer's unconditional-redaction discipline.
            Logger.e { "Uncaught exception in safeScope: ${throwable::class.simpleName}" }
        }

    /**
     * Crash-safe scope for fire-and-forget UI work. Failures that escape the body are caught by an
     * uncaught-exception handler that **logs (the exception type only) and swallows** them — they do
     * not crash the scope, cancel sibling work, or surface on any effect channel. Use it for work
     * that has no other failure path; route user-visible failures through Orbit intents instead.
     */
    public val safeScope: CoroutineScope = viewModelScope + handler

    /**
     * Launches [block] while reflecting its in-flight state through [loading] (reference-counted, so
     * overlapping calls keep the indicator up until all complete).
     *
     * The [CoroutineScope] receiver only selects where the coroutine launches; the loading state is
     * always owned by [viewModelScope]. Call it on [safeScope] so failures are caught by the
     * crash-safe handler rather than propagating.
     */
    // TODO(quality, needs-decision): M1 — the CoroutineScope receiver lets call sites pick
    //  viewModelScope (crash-propagating) or safeScope (swallowing) at random, and does not govern
    //  the loading lifecycle. Collapsing to `fun launchWithLoading(block): Job = safeScope.launch
    //  { ... }` is the right fix but is a breaking change to the committed ABI signature
    //  (foundation.klib.api / foundation.api drop the receiver) and needs the owner's sign-off.
    public fun CoroutineScope.launchWithLoading(block: suspend () -> Unit): Job =
        launch {
            loadingController.withLoading(block)
        }
}
