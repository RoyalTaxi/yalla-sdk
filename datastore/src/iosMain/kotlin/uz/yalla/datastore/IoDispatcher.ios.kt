package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
