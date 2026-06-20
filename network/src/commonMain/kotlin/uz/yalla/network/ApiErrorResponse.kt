package uz.yalla.network

import kotlinx.serialization.Serializable

// TODO(quality, needs-decision): dead public surface — superseded by the internal ApiErrorEnvelope,
//  zero production consumers (only ConventionSmokeTest). Deleting it would shrink the committed
//  .api/.klib.api, a breaking public-API removal that needs the owner's sign-off; leaving it in place
//  until then. If kept, it should become a real documented extension point with a consumer.
@Serializable
public data class ApiErrorResponse(
    val message: String? = null
)
