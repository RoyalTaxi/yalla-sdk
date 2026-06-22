package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

public interface ToggleFactory {
    public fun create(
        initialChecked: Boolean,
        initialEnabled: Boolean,
        colors: ToggleColors,
        onCheckedChange: (Boolean) -> Unit
    ): ToggleHandle
}

public class ToggleColors(
    public val checkedThumbArgb: Long,
    public val checkedTrackArgb: Long,
    public val checkedBorderArgb: Long,
    public val uncheckedThumbArgb: Long,
    public val uncheckedTrackArgb: Long,
    public val uncheckedBorderArgb: Long,
    public val disabledCheckedThumbArgb: Long,
    public val disabledCheckedTrackArgb: Long,
    public val disabledCheckedBorderArgb: Long,
    public val disabledUncheckedThumbArgb: Long,
    public val disabledUncheckedTrackArgb: Long,
    public val disabledUncheckedBorderArgb: Long
)

public object ToggleDefaults {
    public fun colors(
        checkedThumbArgb: Long = 0L,
        checkedTrackArgb: Long = 0L,
        checkedBorderArgb: Long = 0L,
        uncheckedThumbArgb: Long = 0L,
        uncheckedTrackArgb: Long = 0L,
        uncheckedBorderArgb: Long = 0L,
        disabledCheckedThumbArgb: Long = 0L,
        disabledCheckedTrackArgb: Long = 0L,
        disabledCheckedBorderArgb: Long = 0L,
        disabledUncheckedThumbArgb: Long = 0L,
        disabledUncheckedTrackArgb: Long = 0L,
        disabledUncheckedBorderArgb: Long = 0L
    ): ToggleColors =
        ToggleColors(
            checkedThumbArgb = checkedThumbArgb,
            checkedTrackArgb = checkedTrackArgb,
            checkedBorderArgb = checkedBorderArgb,
            uncheckedThumbArgb = uncheckedThumbArgb,
            uncheckedTrackArgb = uncheckedTrackArgb,
            uncheckedBorderArgb = uncheckedBorderArgb,
            disabledCheckedThumbArgb = disabledCheckedThumbArgb,
            disabledCheckedTrackArgb = disabledCheckedTrackArgb,
            disabledCheckedBorderArgb = disabledCheckedBorderArgb,
            disabledUncheckedThumbArgb = disabledUncheckedThumbArgb,
            disabledUncheckedTrackArgb = disabledUncheckedTrackArgb,
            disabledUncheckedBorderArgb = disabledUncheckedBorderArgb
        )
}

public class ToggleHandle(
    public val viewController: UIViewController,
    public val setChecked: (Boolean) -> Unit,
    public val setEnabled: (Boolean) -> Unit,
    public val setColors: (colors: ToggleColors) -> Unit
)
