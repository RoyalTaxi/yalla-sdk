package uz.yalla.components.config.feedback

public interface SnackbarFactory {
    public fun show(
        message: String,
        isError: Boolean
    )

    public fun dismiss()
}
