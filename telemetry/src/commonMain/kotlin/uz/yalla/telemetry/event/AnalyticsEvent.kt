package uz.yalla.telemetry.event

data class AnalyticsEvent(val name: String, val params: Map<String, ParamValue> = emptyMap())

sealed interface ParamValue {
    data class Text(val value: String) : ParamValue
    data class Count(val value: Long) : ParamValue
    data class Amount(val value: Double) : ParamValue
    data class Flag(val value: Boolean) : ParamValue
}

class ParamScope internal constructor() {
    internal val params = LinkedHashMap<String, ParamValue>()

    fun put(key: String, value: String) {
        params[key] = ParamValue.Text(value)
    }

    fun put(key: String, value: Long) {
        params[key] = ParamValue.Count(value)
    }

    fun put(key: String, value: Int) {
        params[key] = ParamValue.Count(value.toLong())
    }

    fun put(key: String, value: Double) {
        params[key] = ParamValue.Amount(value)
    }

    fun put(key: String, value: Boolean) {
        params[key] = ParamValue.Flag(value)
    }
}

fun event(name: String, params: ParamScope.() -> Unit = {}): AnalyticsEvent = AnalyticsEvent(name, ParamScope().apply(params).params)
