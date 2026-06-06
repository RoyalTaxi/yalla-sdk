package uz.yalla.foundation.infra

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

abstract class BaseViewModel : ViewModel() {
    private val loadingController = LoadingController(viewModelScope)
    val loading: StateFlow<Boolean> = loadingController.loading

    private val handler = CoroutineExceptionHandler { _, throwable ->
        Logger.e(throwable) { "Uncaught exception in safeScope" }
    }

    val safeScope: CoroutineScope = viewModelScope + handler

    fun CoroutineScope.launchWithLoading(block: suspend () -> Unit) = launch {
        loadingController.withLoading(block)
    }
}
