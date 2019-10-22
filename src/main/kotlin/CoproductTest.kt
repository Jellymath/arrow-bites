import arrow.generic.coproduct2.Coproduct2
import arrow.generic.coproduct2.second

fun main() {
    val second: Coproduct2<String, Int> = 5.second<String, Int>()
}