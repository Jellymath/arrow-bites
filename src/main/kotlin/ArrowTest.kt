import arrow.Kind
import arrow.core.*
import arrow.typeclasses.MonadThrow

data class User(val name: String)

interface HkUserRepository<F> {
    fun user(username: String): Kind<F, Option<User>>
}

class IdRepository : HkUserRepository<ForId> {
    override fun user(username: String): Id<Option<User>> =
        Id.just(
            if (username == "known") Some(User(username)) else None
        )
}


class UserService<F>(ME: MonadThrow<F>, val repo: HkUserRepository<F>) : MonadThrow<F> by ME {
    fun someOperations(username: String): Kind<F, User> =
        fx.monadThrow {
            val (user) = repo.user(username)
            user.getOrElse { User("Lol default") }
        }
}