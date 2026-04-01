package uz.yalla.core.util

/**
 * Replaces indexed placeholders (`{0}`, `{1}`, ...) with the provided arguments.
 *
 * A lightweight alternative to `String.format` that works identically on
 * all Kotlin Multiplatform targets without JVM dependency.
 *
 * ## Usage
 * ```kotlin
 * val template = "Hello, {0}! You have {1} messages."
 * val result = template.formatArgs("Alice", 5)
 * // Result: "Hello, Alice! You have 5 messages."
 * ```
 *
 * @param args Arguments to replace placeholders; each arg's [Any.toString] is used
 * @return Formatted string with all matching placeholders replaced
 * @since 0.0.1
 */
fun String.formatArgs(vararg args: Any) =
    args.foldIndexed(this) { index, string, arg ->
        string.replace("{$index}", "$arg")
    }
