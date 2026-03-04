package uz.yalla.data.mapper

import uz.yalla.core.order.ServiceBrand
import uz.yalla.data.remote.model.BrandServiceRemoteModel
import uz.yalla.core.util.or0

object ServiceBrandMapper {
    val mapper: Mapper<BrandServiceRemoteModel?, ServiceBrand> = { remote ->
        ServiceBrand(
            id = remote?.id.or0(),
            name = remote?.name.orEmpty(),
            photo = remote?.photo.orEmpty()
        )
    }
}
