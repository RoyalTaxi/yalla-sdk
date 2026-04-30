package uz.yalla.foundation.infra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
import uz.yalla.resources.error_client_request
import uz.yalla.resources.error_connection_timeout
import uz.yalla.resources.error_data_format
import uz.yalla.resources.error_network_unexpected
import uz.yalla.resources.error_no_internet
import uz.yalla.resources.error_server_busy
import uz.yalla.resources.error_unexpected
import kotlin.time.Duration

/**
 * Base ViewModel providing common functionality for all ViewModels.
 *
 * Features:
 * - Smart loading state management via [LoadingController]
 * - Centralized exception handling with error dialog support
 * - Safe coroutine scope with automatic error handling
 * - DataError to StringResource mapping
 *
 * ## Usage
 *
 * ```kotlin
 * class MyViewModel(
 *     private val repository: MyRepository
 * ) : BaseViewModel() {
 *
 *     fun loadData() {
 *         safeScope.launchWithLoading {
 *             repository.fetchData()
 *                 .onSuccess { data -> /* handle success */ }
 *                 .onFailure { error -> handleError(error) }
 *         }
 *     }
 * }
 * ```
 *
 * @see LoadingController for loading state management details
 */
abstract class BaseViewModel : ViewModel() {
    private val loadingController = LoadingController()

    val loading: StateFlow<Boolean> = loadingController.loading

    private val _showErrorDialog = MutableStateFlow(false)

    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog.asStateFlow()

    private val _currentErrorMessageId = MutableStateFlow<StringResource?>(null)

    val currentErrorMessageId: StateFlow<StringResource?> = _currentErrorMessageId.asStateFlow()

    private val handler =
        CoroutineExceptionHandler { _, e ->
            val messageId = mapThrowableToUserMessage(e)
            _currentErrorMessageId.tryEmit(messageId)
            _showErrorDialog.tryEmit(true)
        }

    /**
     * Safe coroutine scope with automatic exception handling.
     *
     * Exceptions thrown in this scope are caught and mapped to user-friendly messages.
     */
    val safeScope: CoroutineScope = viewModelScope + handler

    /**
     * Surfaces an unexpected [Throwable] as an error dialog.
     *
     * Use for exceptions that escape typed [DataError] handling (e.g. unchecked
     * runtime failures). For domain-typed errors, prefer [handleDataError].
     * Coroutines launched in [safeScope] get this automatically via the
     * [CoroutineExceptionHandler]; call this explicitly only from `catch`
     * blocks where the throwable is already in hand.
     */
    fun handleException(throwable: Throwable) {
        val messageId = mapThrowableToUserMessage(throwable)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    /**
     * Surfaces a [DataError] as an error dialog using [mapDataErrorToUserMessage].
     *
     * Prefer over [handleException] whenever the error comes from a typed
     * `Either<DataError, T>` result. Override [mapDataErrorToUserMessage] in
     * the subclass to remap specific variants (e.g. `ClientWithMessage`) to a
     * server-supplied string.
     */
    fun handleDataError(error: DataError) {
        val messageId = mapDataErrorToUserMessage(error)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    /**
     * Hides the error dialog and clears [currentErrorMessageId].
     *
     * Wire to the dialog's dismiss / confirm callback. Safe to call when no
     * dialog is showing — both flows are set atomically via `tryEmit`.
     */
    fun dismissErrorDialog() {
        _showErrorDialog.tryEmit(false)
        _currentErrorMessageId.tryEmit(null)
    }

    /**
     * Launches coroutine with loading state management.
     *
     * Loading indicator appears after delay and stays visible for minimum time.
     */
    fun CoroutineScope.launchWithLoading(
        showAfter: Duration = LoadingController.DEFAULT_SHOW_AFTER,
        minDisplayTime: Duration = LoadingController.DEFAULT_MIN_DISPLAY_TIME,
        block: suspend () -> Unit,
    ) = launch {
        loadingController.withLoading(showAfter, minDisplayTime, block)
    }

    /**
     * Launches a coroutine in this scope with the shared [CoroutineExceptionHandler].
     *
     * Errors surface as an error dialog (same path as [safeScope]). Unlike
     * [launchWithLoading], no loading indicator is shown — use this for
     * background work that must not affect the loading state (analytics,
     * fire-and-forget writes).
     */
    fun CoroutineScope.launchSafe(block: suspend () -> Unit) =
        launch(handler) {
            block()
        }

    /**
     * Override to customize error mapping for app-specific error types.
     *
     * Default mapping covers every [DataError.Network] variant; override to
     * narrow specific cases (e.g., `Network.ClientWithMessage` to a
     * server-supplied message resource) or to handle custom DataError
     * subtypes a feature module introduces.
     */
    protected open fun mapDataErrorToUserMessage(error: DataError): StringResource =
        when (error) {
            DataError.Network.Connection -> Res.string.error_no_internet
            DataError.Network.Timeout -> Res.string.error_connection_timeout
            DataError.Network.Client -> Res.string.error_client_request
            is DataError.Network.ClientWithMessage -> Res.string.error_client_request
            DataError.Network.Server -> Res.string.error_server_busy
            DataError.Network.Serialization -> Res.string.error_data_format
            DataError.Network.Guest -> Res.string.error_client_request
            DataError.Network.Unknown -> Res.string.error_network_unexpected
        }

    /**
     * Only handles genuine exceptions (not DataError, which has its own handler).
     */
    protected open fun mapThrowableToUserMessage(throwable: Throwable): StringResource = Res.string.error_unexpected
}
