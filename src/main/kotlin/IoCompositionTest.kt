import arrow.core.Option
import arrow.core.extensions.fx
import arrow.core.extensions.traverse
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.applicative.applicative

enum class AccessRight {
    ALLOWED, RESTRICTED, FORBIDDEN
}

enum class Role {
    GUEST, USER, MODERATOR, ADMIN;

    val id get() = ordinal.toLong()
}

data class RoleTo(val role: Role, val accessRight: List<AccessRight>)

interface UserRepository {
    fun role(codename: String): IO<Option<Role>>

    fun accessRights(roleId: Long): IO<List<AccessRight>>
}

class IoUserService(val userRepository: UserRepository) {
    private fun retrieveRole(roleCodename: String): IO<Option<RoleTo>> =
        IO.fx {
            val (role) = userRepository.role(roleCodename)
            val (accessRights) = role.traverse(IO.applicative()) { userRepository.accessRights(it.id) }
//            Option.fx { RoleTo(role.bind(), accessRights.bind()) }
            role.map2(accessRights) { (r, ar) -> RoleTo(r, ar) }
        }
}