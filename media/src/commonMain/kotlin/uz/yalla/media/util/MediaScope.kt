package uz.yalla.media.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal val MediaScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
