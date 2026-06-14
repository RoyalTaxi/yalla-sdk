package uz.yalla.components.feedback

import uz.yalla.components.config.requireConfig

internal actual fun yallaShowSnackbar(
    message: String,
    isError: Boolean
) {
    requireConfig().snackbar.show(message, isError)
}

internal actual fun yallaDismissSnackbar() {
    requireConfig().snackbar.dismiss()
}
