package uz.yalla.components.util

/**
 * Format string with indexed placeholders.
 *
 * ## Usage
 *
 * ```kotlin
 * val template = "Hello, {0}! You have {1} messages."
 * val result = template.formatArgs("Alice", 5)
 * // Result: "Hello, Alice! You have 5 messages."
 * ```
 *
 * @param args Arguments to replace placeholders
 * @return Formatted string
 */
fun String.formatArgs(vararg args: Any) =
    args.foldIndexed(this) { index, string, arg ->
        string.replace("{$index}", "$arg")
    }
