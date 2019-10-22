import arrow.Kind
import arrow.core.*
import arrow.core.extensions.list.foldable.firstOption
import arrow.generic.*
import arrow.typeclasses.Const
import arrow.typeclasses.ConstPartialOf

interface PolyFunction<F, G> {
    operator fun <T> invoke(t: Kind<F, T>): Kind<G, T>
}

object headOption : PolyFunction<ForListK, ForOption> {
    override fun <T> invoke(t: ListKOf<T>) = t.fix().firstOption()
}

fun <F, G, T> PolyFunction<F, G>.mono() = { it: Kind<F, T> -> this(it) }

fun main() {
    val lists = listOf(
        listOf(1, 2, 3).k(),
        ListK.empty(),
        ListK.just(2)
    )
    println(lists.map { headOption(it) })
    println(lists.map(headOption.mono<ForListK, ForOption, Int>())) //works fine
//    println(lists.map(headOption.mono())) //org.jetbrains.kotlin.codegen.CompilationException: Back-end (JVM) Internal error


    val hList = hListOf(1, "abc", true)
    println(hList.map(singleton))
}

@JvmName("hMap0")
fun <Out> HNil.map(action: PolyFunction<ForId, Out>) = HNil
@JvmName("hMap1")
fun <A, Out> HList1<A>.map(action: PolyFunction<ForId, Out>) = HCons(action(head), HNil)
@JvmName("hMap2")
fun <A, B, Out> HList2<A, B>.map(action: PolyFunction<ForId, Out>) = HCons(action(head), tail.map(action))
@JvmName("hMap3")
fun <A, B, C, Out> HList3<A, B, C>.map(action: PolyFunction<ForId, Out>) = HCons(action(head), tail.map(action))

//    println(zero("abc"))
//    println(headOption(listOf(1, 2, 3).k()))


object singletonList : PolyFunction<ForId, ForListK> {
    override fun <T> invoke(t: IdOf<T>) = ListK.just(t.value())
}

object singleton : PolyFunction<ForId, ForSetK> {
    override fun <T> invoke(t: IdOf<T>) = SetK.just(t.value())
}

object identity : PolyFunction<ForId, ForId> {
    override fun <T> invoke(t: IdOf<T>) = t
}


object zero : PolyFunction<ForId, ConstPartialOf<Int>> {
    override fun <T> invoke(t: IdOf<T>) = Const<Int, T>(0)
}

object size : PolyFunction<ForId, ConstPartialOf<Int>> {
    override fun <T> invoke(t: IdOf<T>) =
        when (val value = t.value()) {
            is List<*> -> value.size
            is String -> value.length
            else -> 0
        }.let { Const<Int, T>(it) }
}

operator fun <G, T> PolyFunction<ForId, G>.invoke(t: T) = invoke(Id.just(t))
