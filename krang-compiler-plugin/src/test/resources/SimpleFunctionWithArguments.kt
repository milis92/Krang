import com.herman.krang.runtime.annotations.Intercept

fun main() {
    simpleFunctionWithArguments(1, "test")
}

@Intercept
fun simpleFunctionWithArguments(int: Int, string:String) {}
