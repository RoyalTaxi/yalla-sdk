package uz.yalla.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

public expect fun createDataStore(): DataStore<Preferences>
