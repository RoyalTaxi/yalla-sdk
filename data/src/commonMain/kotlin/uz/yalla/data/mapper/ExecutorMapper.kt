package uz.yalla.data.mapper

import uz.yalla.core.model.Executor
import uz.yalla.data.remote.model.ExecutorRemoteModel
import uz.yalla.data.util.or0

object ExecutorMapper {
    val mapper: Mapper<ExecutorRemoteModel?, Executor> = { remote ->
        Executor(
            id = remote?.id.or0(),
            lat = remote?.lat.or0(),
            lng = remote?.lng.or0(),
            heading = remote?.heading.or0(),
            distance = remote?.distance.or0()
        )
    }
}
