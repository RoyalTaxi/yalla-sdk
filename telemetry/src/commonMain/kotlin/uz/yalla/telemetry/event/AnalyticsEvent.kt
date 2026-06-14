package uz.yalla.telemetry.event

public data class AnalyticsEvent(
    val name: String,
    val params: Map<String, ParamValue> = emptyMap()
)

public sealed interface ParamValue {
    public data class Text(
        val value: String
    ) : ParamValue

    public data class Count(
        val value: Long
    ) : ParamValue

    public data class Amount(
        val value: Double
    ) : ParamValue

    public data class Flag(
        val value: Boolean
    ) : ParamValue
}

public class ParamScope internal constructor() {
    internal val params = LinkedHashMap<String, ParamValue>()

    public fun put(
        key: String,
        value: String
    ) {
        params[key] = ParamValue.Text(value)
    }

    public fun put(
        key: String,
        value: Long
    ) {
        params[key] = ParamValue.Count(value)
    }

    public fun put(
        key: String,
        value: Int
    ) {
        params[key] = ParamValue.Count(value.toLong())
    }

    public fun put(
        key: String,
        value: Double
    ) {
        params[key] = ParamValue.Amount(value)
    }

    public fun put(
        key: String,
        value: Boolean
    ) {
        params[key] = ParamValue.Flag(value)
    }
}

public fun event(
    name: String,
    params: ParamScope.() -> Unit = {
    }
): AnalyticsEvent = AnalyticsEvent(name, ParamScope().apply(params).params)
