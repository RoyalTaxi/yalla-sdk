package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext

@OptIn(DelicateCoroutinesApi::class)
@Suppress("InjectDispatcher")
public actual val ioDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(nThreads = 8, name = "yalla-io")
