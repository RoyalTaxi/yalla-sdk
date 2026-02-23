package uz.yalla.firebase.logging

fun interface YallaFirebaseLogger {
    fun log(
        tag: String,
        message: String
    )

    companion object {
        val Noop = YallaFirebaseLogger { _, _ -> }
    }
}
