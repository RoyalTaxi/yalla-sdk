package uz.yalla.data.mapper

import uz.yalla.core.model.Client
import uz.yalla.data.remote.model.ClientRemoteModel
import uz.yalla.data.util.or0

object ClientMapper {
    val clientMapper: Mapper<ClientRemoteModel?, Client> =
        { client ->
            Client(
                phone = client?.phone.orEmpty(),
                name = client?.given_names.orEmpty(),
                surname = client?.sur_name.orEmpty(),
                image = client?.image.orEmpty(),
                birthday = client?.birthday.orEmpty(),
                balance = client?.balance.or0(),
                gender = client?.gender.orEmpty()
            )
        }
}
