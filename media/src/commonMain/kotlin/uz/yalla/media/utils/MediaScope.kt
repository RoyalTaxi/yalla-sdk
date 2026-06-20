package uz.yalla.media.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * App-lifetime scope the launchers use to read picked/captured bytes. The native pickers outlive the
 * caller's composition scope, so a picked image must not be dropped when the user navigates away
 * while the OS picker is still open. Reads run here (independent of any UI lifecycle) and only the
 * final callback hops back to the caller's context.
 */
internal val MediaScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
