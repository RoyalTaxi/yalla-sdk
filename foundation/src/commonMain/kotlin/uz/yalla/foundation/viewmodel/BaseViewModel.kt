package uz.yalla.foundation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.compose.resources.StringResource
import uz.yalla.core.error.DataError
import uz.yalla.resources.Res
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
abstract class BaseViewModel(
    private val dataErrorMapper: DataErrorMapper = DefaultDataErrorMapper(),
) : ViewModel() {
    private val loadingController = LoadingController()

    /** Loading state flow. Observe to show/hide loading indicators. */
    val loading: StateFlow<Boolean> = loadingController.loading

    private val _failure = Channel<Int>(Channel.UNLIMITED)

    /** Flow of failure codes for external observation. */
    val failure: Flow<Int> = _failure.receiveAsFlow()

    private val _showErrorDialog = MutableStateFlow(false)

    /** Whether error dialog should be visible. */
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog.asStateFlow()

    private val _currentErrorMessageId = MutableStateFlow<StringResource?>(null)

    /** Current error message resource to display in dialog. */
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
     * Handles exception by showing error dialog with mapped message.
     *
     * @param throwable The exception that occurred
     */
    fun handleException(throwable: Throwable) {
        val messageId = mapThrowableToUserMessage(throwable)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    /**
     * Handles [DataError] by showing error dialog with mapped message.
     *
     * @param error The data error that occurred
     */
    fun handleDataError(error: DataError) {
        val messageId = mapDataErrorToUserMessage(error)
        _currentErrorMessageId.tryEmit(messageId)
        _showErrorDialog.tryEmit(true)
    }

    /** Dismisses the currently shown error dialog. */
    fun dismissErrorDialog() {
        _showErrorDialog.tryEmit(false)
        _currentErrorMessageId.tryEmit(null)
    }

    /**
     * Launches coroutine with loading state management.
     *
     * Loading indicator appears after delay and stays visible for minimum time.
     *
     * @param showAfter Delay before showing loading. Defaults to controller's configured value.
     * @param minDisplayTime Minimum display time once shown. Defaults to controller's configured value.
     * @param block Suspending operation to execute
     */
    fun CoroutineScope.launchWithLoading(
        showAfter: Duration = LoadingController.DEFAULT_SHOW_AFTER,
        minDisplayTime: Duration = LoadingController.DEFAULT_MIN_DISPLAY_TIME,
        block: suspend () -> Unit,
    ) = launch {
        loadingController.withLoading(showAfter, minDisplayTime, block)
    }

    /**
     * Launches coroutine with automatic exception handling.
     *
     * @param block Suspending operation to execute
     */
    fun CoroutineScope.launchSafe(block: suspend () -> Unit) =
        launch(handler) {
            block()
        }

    /**
     * Maps [DataError] to user-friendly message resource.
     *
     * Override to customize error mapping for app-specific error types.
     *
     * @param error The data error to map
     * @return StringResource for the error message
     */
    protected open fun mapDataErrorToUserMessage(error: DataError): StringResource = dataErrorMapper.map(error)

    /**
     * Maps throwable to user-friendly message resource.
     *
     * Only handles genuine exceptions (not DataError, which has its own handler).
     *
     * @param throwable The exception to map
     * @return StringResource for the error message
     */
    protected open fun mapThrowableToUserMessage(throwable: Throwable): StringResource = Res.string.error_unexpected
}
