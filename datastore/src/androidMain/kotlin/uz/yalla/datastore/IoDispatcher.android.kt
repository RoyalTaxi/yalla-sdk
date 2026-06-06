package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Suppress("InjectDispatcher")
actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
