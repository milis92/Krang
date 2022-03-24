import com.herman.krang.runtime.FunctionInterceptor
import com.herman.krang.runtime.Krang

fun iniatliseKrang(interceptor: FunctionInterceptor) {
    Krang.addInterceptor(interceptor)
}