package uz.yalla.components.foundation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Launches a coroutine in the ViewModel scope with loading state management.
 *
 * @param loadingController Controller managing loading state
 * @param block Suspending operation to execute
 */
fun ViewModel.launchWithLoading(
    loadingController: LoadingController,
    block: suspend () -> Unit,
) {
    viewModelScope.launch {
        loadingController.withLoading { block() }
    }
}

/**
 * Extension to launch with loading from any CoroutineScope.
 *
 * @param loadingController Controller managing loading state
 * @param block Suspending operation to execute
 */
fun CoroutineScope.launchWithLoading(
    loadingController: LoadingController,
    block: suspend () -> Unit,
) = launch {
    loadingController.withLoading { block() }
}
