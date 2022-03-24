import com.herman.krang.runtime.annotations.Intercept

fun main() {
    iniatliseKrang { funtionName, arguments: Array<out Any?> ->
        println("Function with $funtionName called with arguments: ${arguments.joinToString()}")
    }
    Foo().bar("baz")
}

class Foo {
    @Intercept
    fun bar(@Suppress("UNUSED_PARAMETER") param1: String) {
        /* no-op */
    }
}