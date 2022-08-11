@file:Suppress("UnnecessaryVariable")

package fixtures

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language

fun String.compile(
    compiler: KotlinCompilation,
    fileName: String = "Main.kt",
    compilationResult: KotlinCompilation.Result.() -> Unit
) {
    compiler.sources = listOf(SourceFile.kotlin(fileName, this, true))
    val result = compiler.compile()
    compilationResult(result)
}

private fun List<Any?>?.toFunctionArguments(redactAtIndex: Int? = null): String {
    return if (isNullOrEmpty()) {
        ""
    } else {
        joinToString {
            """${if (redactAtIndex == indexOf(it)) "@Redact" else ""} 
                |arg${indexOf(it)} : ${it!!::class.qualifiedName ?: Any::class::qualifiedName}
            """.trimMargin()
        }
    }
}

fun simpleClassFunction(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

class Main {
    @Intercept
    fun foo(${arguments.toFunctionArguments()}){
                        
    }
}
    """
    return output
}

val topLevelFunction: String
    @Language("kotlin")
    get() = """
import com.herman.krang.runtime.annotations.Intercept

@Intercept
fun main(){}
"""

fun functionWithoutBody(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

class Main { 
    @Intercept
    fun foo(${arguments.toFunctionArguments()}) = true
}
"""
    return output
}

fun scopeFunction(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

class Main { 
    @Intercept
    fun foo(${arguments.toFunctionArguments()}) = run {}
}
"""
    return output
}

// TODO Add support for passing arguments to inner function
fun innerFunction(@Suppress("UNUSED_PARAMETER") arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

class Main {
    fun foo() { 
        @Intercept 
        fun innerFunction(){}
        innerFunction() 
    }
}
"""
    return output
}

fun extensionFunction(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

@Intercept
fun String.foo(${arguments.toFunctionArguments()}) {}
"""
    return output
}

fun overriddenFunction(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

interface Testable {
    fun test(${arguments.toFunctionArguments()})
}

class Main : Testable {
    @Intercept
    override fun test(${arguments.toFunctionArguments()}){}
}
"""
    return output
}

fun functionWithAnnotatedParent(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept
    
interface Testable {
    @Intercept
    fun test(${arguments.toFunctionArguments()})
}

class Main : Testable {
    override fun test(${arguments.toFunctionArguments()}){}
}
"""
    return output
}

fun functionWithRedactedParameter(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept
import com.herman.krang.runtime.annotations.Redact

class Main {
    @Intercept
    fun foo(${arguments.toFunctionArguments(0)}){}
}
"""
    return output
}

fun functionWithRedactedParentParameter(): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept
import com.herman.krang.runtime.annotations.Redact

data class Test(val test:Int = 1)

class Main {
    @Intercept
    fun foo(test: Test){}
}
"""
    return output
}

fun simpleClass(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

@Intercept
class Main {
    fun foo(${arguments.toFunctionArguments()}){}
}
"""
    return output
}

fun annotatedClassWithAnnotatedFunction(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

@Intercept
class Main {
    @Intercept
    fun foo(${arguments.toFunctionArguments()}){}
}
"""
    return output
}

fun simpleClassWithAnnotatedParent(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
import com.herman.krang.runtime.annotations.Intercept

@Intercept
interface Testable

@Intercept
class Main : Testable{
    fun foo(${arguments.toFunctionArguments()}){}
}
"""
    return output
}

fun classWithFunctionWithoutInterceptAnnotation(arguments: List<Any?>? = null): String {
    @Language("kotlin")
    val output = """
class Main {
    fun foo(${arguments.toFunctionArguments()}){}
}
"""
    return output
}
