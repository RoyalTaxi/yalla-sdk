package uz.yalla.carto.capability.framework

public class CapabilityKey<H : CapabilityHandle>(
    public val name: String
) {
    override fun toString(): String = "CapabilityKey($name)"
}
