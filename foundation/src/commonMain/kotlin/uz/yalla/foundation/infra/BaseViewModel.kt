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

public abstract class BaseViewModel : ViewModel() {
    private val loadingController = LoadingController(viewModelScope)
    public val loading: StateFlow<Boolean> = loadingController.loading

    private val handler =
        CoroutineExceptionHandler { _, throwable ->
            Logger.e(throwable) { "Uncaught exception in safeScope" }
        }

    public val safeScope: CoroutineScope = viewModelScope + handler

    public fun CoroutineScope.launchWithLoading(block: suspend () -> Unit): Job =
        launch {
            loadingController.withLoading(block)
        }
}
