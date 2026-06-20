package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// The single intentional dispatcher-definition site for the SDK's IO context; see the iOS actual for the
// Kotlin/Native equivalent (a dedicated pool, since K/N has no Dispatchers.IO).
@Suppress("InjectDispatcher")
public actual val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
