package uz.yalla.firebase.analytics

import uz.yalla.firebase.YallaFirebase

fun trackEvent(name: String, params: Map<String, Any> = emptyMap()) {
    YallaFirebase.analytics.logEvent(name, params.ifEmpty { null })
}
