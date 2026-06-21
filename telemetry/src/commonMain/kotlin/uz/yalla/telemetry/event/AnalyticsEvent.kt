package uz.yalla.telemetry.event

/**
 * A single analytics event: a [name] plus typed [params]. Construct via the
 * [event] DSL rather than this constructor; sinks read [name]/[params] only.
 */
public class AnalyticsEvent(
    public val name: String,
    public val params: Map<String, ParamValue> = emptyMap()
) {
    override fun equals(other: Any?): Boolean =
        this === other || (other is AnalyticsEvent && name == other.name && params == other.params)

    override fun hashCode(): Int = 31 * name.hashCode() + params.hashCode()

    override fun toString(): String = "AnalyticsEvent(name=$name, params=$params)"
}

/** Typed analytics parameter value. Keeps the wire payload type-safe per sink. */
public sealed interface ParamValue {
    /**
     * A free-form string value. Must never carry PII or raw user input (search
     * queries, addresses, names, card data) — the value reaches third-party analytics.
     */
    public class Text(
        public val value: String
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Text && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Text(value=$value)"
    }

    /** An integral count. [Int] inputs are widened to [Long] by [ParamScope.put]. */
    public class Count(
        public val value: Long
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Count && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Count(value=$value)"
    }

    /** A fractional/monetary amount. */
    public class Amount(
        public val value: Double
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Amount && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Amount(value=$value)"
    }

    /** A boolean flag. */
    public class Flag(
        public val value: Boolean
    ) : ParamValue {
        override fun equals(other: Any?): Boolean = this === other || (other is Flag && value == other.value)

        override fun hashCode(): Int = value.hashCode()

        override fun toString(): String = "Flag(value=$value)"
    }
}

/**
 * Builder scope for [event] params. Backed by an insertion-ordered map, so params
 * keep their declared order and a duplicate key is last-write-wins. Each [put]
 * overload maps to the matching [ParamValue] subtype.
 */
public class ParamScope internal constructor() {
    internal val params = LinkedHashMap<String, ParamValue>()

    /** Adds a [ParamValue.Text] param. [value] must not be PII or raw user input. */
    public fun put(
        key: String,
        value: String
    ) {
        params[key] = ParamValue.Text(value)
    }

    /** Adds a [ParamValue.Count] param. */
    public fun put(
        key: String,
        value: Long
    ) {
        params[key] = ParamValue.Count(value)
    }

    /** Adds a [ParamValue.Count] param, widening [value] to [Long]. */
    public fun put(
        key: String,
        value: Int
    ) {
        params[key] = ParamValue.Count(value.toLong())
    }

    /** Adds a [ParamValue.Amount] param. */
    public fun put(
        key: String,
        value: Double
    ) {
        params[key] = ParamValue.Amount(value)
    }

    /** Adds a [ParamValue.Flag] param. */
    public fun put(
        key: String,
        value: Boolean
    ) {
        params[key] = ParamValue.Flag(value)
    }
}

/**
 * Builds an [AnalyticsEvent] named [name] with params declared in [params]. The
 * canonical construction path; sinks consume the resulting [AnalyticsEvent].
 */
public fun event(
    name: String,
    params: ParamScope.() -> Unit = {
    }
): AnalyticsEvent = AnalyticsEvent(name, ParamScope().apply(params).params)
