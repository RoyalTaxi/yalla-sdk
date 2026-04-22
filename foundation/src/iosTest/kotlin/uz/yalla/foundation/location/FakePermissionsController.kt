package uz.yalla.foundation.location

import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.ios.PermissionsControllerProtocol

/**
 * Minimal fake [PermissionsControllerProtocol] for use in iosTest.
 *
 * On iOS, [dev.icerock.moko.permissions.PermissionsController] is a typealias for
 * [PermissionsControllerProtocol], which is an interface — so it's safe to implement here
 * without pulling in platform location infrastructure.
 *
 * [providePermission] returns immediately (grants permission silently).
 * [getPermissionState] always returns [PermissionState.Granted].
 */
class FakePermissionsController : PermissionsControllerProtocol {
    override suspend fun providePermission(permission: Permission) {
        // No-op: permission always considered granted in tests.
    }

    override suspend fun isPermissionGranted(permission: Permission): Boolean = true

    override suspend fun getPermissionState(permission: Permission): PermissionState =
        PermissionState.Granted

    override fun openAppSettings() {
        // No-op
    }
}
