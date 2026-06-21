package uz.yalla.capabilities

import android.content.Context
import org.koin.core.context.GlobalContext

/**
 * Single seam through which this module's `actual` platform glue obtains the application [Context].
 *
 * The common `expect` signatures (e.g. `isLocationServicesEnabled()`, `getAppSignature()`) are
 * Context-free, so the Android side has to source it out-of-band. It is pulled from the Koin
 * container — a service-locator read, deliberately confined to this one accessor so that swapping it
 * for an explicit startup-time injection later is a single-file change. Use [capabilitiesContextOrNull]
 * from code that may run before Koin is started.
 */
internal val capabilitiesContext: Context
    get() = GlobalContext.get().get()

internal val capabilitiesContextOrNull: Context?
    get() = runCatching { capabilitiesContext }.getOrNull()
