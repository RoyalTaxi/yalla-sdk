package uz.yalla.core.util

fun String.formatArgs(vararg args: Any) = args.foldIndexed(this) { index, string, arg ->
    string.replace("{$index}", "$arg")
}
