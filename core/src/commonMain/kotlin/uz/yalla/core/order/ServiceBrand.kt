package uz.yalla.core.order

import uz.yalla.core.identity.ServiceBrandId

public data class ServiceBrand(
    val id: ServiceBrandId,
    val name: String,
    val photo: String
)
