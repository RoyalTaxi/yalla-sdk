package uz.yalla.components.feedback

public object Snackbar {
    public fun show(
        message: String,
        isError: Boolean = false
    ): Unit = yallaShowSnackbar(message, isError)

    public fun showSuccess(message: String): Unit = show(message, isError = false)

    public fun showError(message: String): Unit = show(message, isError = true)

    public fun dismiss(): Unit = yallaDismissSnackbar()
}

internal expect fun yallaShowSnackbar(
    message: String,
    isError: Boolean
)

internal expect fun yallaDismissSnackbar()
