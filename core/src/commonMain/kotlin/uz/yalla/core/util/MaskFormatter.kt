package uz.yalla.core.util

object MaskFormatter {
    fun format(text: String, mask: String): String {
        if (text.isEmpty()) return ""
        val builder = StringBuilder()
        var index = 0
        for (token in mask) {
            if (index >= text.length) break
            if (token == '_') {
                builder.append(text[index])
                index++
            } else {
                builder.append(token)
            }
        }
        return builder.toString()
    }
}
