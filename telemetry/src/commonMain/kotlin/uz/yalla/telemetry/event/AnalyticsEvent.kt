package uz.yalla.telemetry.event

// TODO(quality, needs-decision): AnalyticsEvent and the ParamValue subtypes are
//  `data class`es, so copy()/componentN()/equals/hashCode are part of the committed
//  .api/.klib.api. Demoting them to regular classes shrinks the breakable ABI but is
//  a breaking removal (b-c-v gate) — defer to the owner before 1.0 leaves alpha.
//  Construction is meant to go through `event { }`; consumers should read .name/.params
//  and not destructure or `copy`.

/**
 * A single analytics event: a [name] plus typed [params]. Construct via the
 * [event] DSL rather than this constructor; sinks read [name]/[params] only.
 */
public data class AnalyticsEvent(
    val name: String,
    val params: Map<String, ParamValue> = emptyMap()
)

/** Typed analytics parameter value. Keeps the wire payload type-safe per sink. */
public sealed interface ParamValue {
    /**
     * A free-form string value. Must never carry PII or raw user input (search
     * queries, addresses, names, card data) — the value reaches third-party analytics.
     */
    public data class Text(
        val value: String
    ) : ParamValue

    /** An integral count. [Int] inputs are widened to [Long] by [ParamScope.put]. */
    public data class Count(
        val value: Long
    ) : ParamValue

    /** A fractional/monetary amount. */
    public data class Amount(
        val value: Double
    ) : ParamValue

    /** A boolean flag. */
    public data class Flag(
        val value: Boolean
    ) : ParamValue
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
