package uz.yalla.components.config.feedback

interface SnackbarFactory {
    fun show(message: String, isError: Boolean)
    fun dismiss()
}
