package uz.yalla.datastore

import kotlinx.coroutines.CoroutineDispatcher

/**
 * The SDK-wide dispatcher for blocking IO — wrap data-layer IO (network, DAO, DataStore `edit`) in
 * `withContext(ioDispatcher)`.
 *
 * Backed by `Dispatchers.IO` on Android and a dedicated fixed thread pool on iOS (Kotlin/Native has no
 * `Dispatchers.IO`), so blocking work does not contend with CPU-bound coroutine work on the Default pool.
 * It is intentionally never an unbounded CPU dispatcher on either platform.
 */
public expect val ioDispatcher: CoroutineDispatcher
