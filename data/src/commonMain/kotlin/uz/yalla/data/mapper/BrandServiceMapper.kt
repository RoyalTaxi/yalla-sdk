package uz.yalla.data.mapper

import uz.yalla.core.model.BrandServiceModel
import uz.yalla.data.remote.model.BrandServiceRemoteModel
import uz.yalla.data.util.or0

object BrandServiceMapper {
    val mapper: Mapper<BrandServiceRemoteModel?, BrandServiceModel> = { remote ->
        BrandServiceModel(
            id = remote?.id.or0(),
            name = remote?.name.orEmpty(),
            photo = remote?.photo.orEmpty()
        )
    }
}
