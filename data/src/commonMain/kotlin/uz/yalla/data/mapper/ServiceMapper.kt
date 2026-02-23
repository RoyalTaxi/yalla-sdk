package uz.yalla.data.mapper

import uz.yalla.core.model.ServiceModel
import uz.yalla.data.remote.model.ServiceRemoteModel
import uz.yalla.data.util.or0

object ServiceMapper {
    val mapper: Mapper<ServiceRemoteModel?, ServiceModel> = { remote ->
        ServiceModel(
            cost = remote?.cost.or0(),
            costType = remote?.costType ?: ServiceModel.COST_TYPE_COST,
            id = remote?.id.or0(),
            name = remote?.name.orEmpty()
        )
    }
}
