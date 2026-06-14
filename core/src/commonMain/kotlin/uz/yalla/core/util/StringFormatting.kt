package uz.yalla.core.util

public fun String.formatArgs(vararg args: Any): String =
    args.foldIndexed(this) { index, string, arg ->
        string.replace("{$index}", "$arg")
    }
