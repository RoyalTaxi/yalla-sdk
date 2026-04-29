package uz.yalla.data.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher") // this IS the injection point the rule wants callers to use
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
