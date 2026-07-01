package uz.yalla.carto.capability.framework

public interface Capability<H : CapabilityHandle> {
    public val key: CapabilityKey<H>

    public fun create(context: CapabilityContext): H
}
