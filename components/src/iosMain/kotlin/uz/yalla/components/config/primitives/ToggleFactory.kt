package uz.yalla.components.config.primitives

import platform.UIKit.UIViewController

/**
 * iOS bridge for the shared `Toggle`. The Swift side implements this to host a native switch and
 * returns a [ToggleHandle] of imperative closures Compose drives. Each color is packed as a
 * `0xAARRGGBB` [Long] (see `Color.toArgbOrZero()`); `0` means "use the native default".
 *
 * The full restyleable surface the common `Toggle` expect declares (checked/unchecked thumb, track,
 * and border, plus their disabled variants) is carried across the bridge so iOS renders the same
 * colors Android does for any restyle.
 */
public interface ToggleFactory {
    /** Builds the native switch and returns its [ToggleHandle]. */
    public fun create(
        initialChecked: Boolean,
        initialEnabled: Boolean,
        colors: ToggleColors,
        onCheckedChange: (Boolean) -> Unit
    ): ToggleHandle
}

/**
 * The full set of packed-ARGB (`0xAARRGGBB`) colors a native toggle renders, mirroring the common
 * `Toggle` expect's color parameters. `0` for any channel means "use the native default".
 */
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

/** Defaults for the native toggle bridge. A zero channel keeps the Swift-side native default. */
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

/**
 * Handle Compose uses to update a live native switch.
 *
 * @property viewController the hosting native controller.
 * @property setChecked updates the checked state.
 * @property setEnabled updates the enabled state.
 * @property setColors updates the full [ToggleColors] set (each packed `0xAARRGGBB`).
 */
public class ToggleHandle(
    public val viewController: UIViewController,
    public val setChecked: (Boolean) -> Unit,
    public val setEnabled: (Boolean) -> Unit,
    public val setColors: (colors: ToggleColors) -> Unit
)
