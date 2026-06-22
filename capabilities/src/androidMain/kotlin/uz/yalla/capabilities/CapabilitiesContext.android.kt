package uz.yalla.capabilities

import android.content.Context
import org.koin.core.context.GlobalContext

internal val capabilitiesContext: Context
    get() = GlobalContext.get().get()

internal val capabilitiesContextOrNull: Context?
    get() = runCatching { capabilitiesContext }.getOrNull()
