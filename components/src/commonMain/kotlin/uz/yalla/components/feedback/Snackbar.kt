package uz.yalla.components.feedback

object Snackbar {
    fun show(message: String, isError: Boolean = false) = yallaShowSnackbar(message, isError)

    fun showSuccess(message: String) = show(message, isError = false)

    fun showError(message: String) = show(message, isError = true)

    fun dismiss() = yallaDismissSnackbar()
}

internal expect fun yallaShowSnackbar(message: String, isError: Boolean)

internal expect fun yallaDismissSnackbar()
