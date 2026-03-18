# Module firebase

Yalla SDK Firebase integration — Analytics, Crashlytics, Cloud Messaging, and pluggable logging.

All services are accessed through the [YallaFirebase][uz.yalla.firebase.YallaFirebase] singleton,
which must be initialized before use. Operations are wrapped with error handling and logged
via a pluggable [YallaFirebaseLogger][uz.yalla.firebase.logging.YallaFirebaseLogger].

# Package uz.yalla.firebase

Main entry point ([YallaFirebase]) and platform initialization.

# Package uz.yalla.firebase.analytics

Analytics event tracking with predefined ([AnalyticsEvent]) and custom events.

# Package uz.yalla.firebase.crashlytics

Crash reporting and non-fatal exception recording via [YallaCrashlytics].

# Package uz.yalla.firebase.messaging

Push notification token management, topic subscriptions, and [MessagingDelegate] for platform-specific handling.

# Package uz.yalla.firebase.logging

Pluggable logging interface ([YallaFirebaseLogger]) for Firebase wrapper operations.
