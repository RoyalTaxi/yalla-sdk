package uz.yalla.maps.compose

import com.google.android.gms.maps.model.ButtCap
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.maps.model.SquareCap
import uz.yalla.maps.model.Cap
import uz.yalla.maps.model.JointType
import com.google.android.gms.maps.model.Cap as GoogleCap
import com.google.android.gms.maps.model.JointType as GoogleJointType

/** Cached [ButtCap] instance to avoid per-call allocation. */
private val buttCap = ButtCap()

/** Cached [RoundCap] instance to avoid per-call allocation. */
private val roundCap = RoundCap()

/** Cached [SquareCap] instance to avoid per-call allocation. */
private val squareCap = SquareCap()

/**
 * Converts this cross-platform [Cap] to its Google Maps SDK equivalent.
 *
 * @return A cached Google Maps `Cap` instance.
 * @since 0.0.1
 */
internal fun Cap.toGoogleCap(): GoogleCap =
    when (this) {
        Cap.Butt -> buttCap
        Cap.Round -> roundCap
        Cap.Square -> squareCap
    }

/**
 * Converts this cross-platform [JointType] to its Google Maps SDK integer constant.
 *
 * @return One of `GoogleJointType.DEFAULT`, `BEVEL`, or `ROUND`.
 * @since 0.0.1
 */
internal fun JointType.toGoogleJointType(): Int =
    when (this) {
        JointType.Default -> GoogleJointType.DEFAULT
        JointType.Bevel -> GoogleJointType.BEVEL
        JointType.Round -> GoogleJointType.ROUND
    }
