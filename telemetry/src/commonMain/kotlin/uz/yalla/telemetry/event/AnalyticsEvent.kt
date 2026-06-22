package uz.yalla.telemetry.event

public class AnalyticsEvent(
    public val name: String,
    public val params: Map<String, ParamValue> = emptyMap()
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is AnalyticsEvent && name == other.name && params == other.params)

    override fun hashCode(): Int = 31 * name.hashCode() + params.hashCode()

    override fun toString(): String = "AnalyticsEvent(name=$name, params=$params)"
}

public sealed interface ParamValue {
    public class Text(
        public val value: String
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Text && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Text(value=$value)"
    }

    public class Count(
        public val value: Long
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Count && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Count(value=$value)"
    }

    public class Amount(
        public val value: Double
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Amount && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Amount(value=$value)"
    }

    public class Flag(
        public val value: Boolean
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Flag && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Flag(value=$value)"
    }
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
