package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.newFixedThreadPoolContext

// Kotlin/Native has no Dispatchers.IO (coroutines 1.11.0), so back the IO dispatcher with a dedicated
// fixed thread pool rather than Dispatchers.Default — DataStore's `edit` does blocking file IO and must
// not contend with CPU-bound coroutine work (Flow operators, geo/motion math) on the core-count-sized
// Default pool. App-lifetime singleton; the threads are deliberately never released.
@OptIn(DelicateCoroutinesApi::class)
@Suppress("InjectDispatcher")
public actual val ioDispatcher: CoroutineDispatcher = newFixedThreadPoolContext(nThreads = 8, name = "yalla-io")
