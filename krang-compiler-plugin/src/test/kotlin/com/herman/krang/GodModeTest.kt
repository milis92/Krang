package com.herman.krang

import com.herman.krang.assertions.assertCompilation
import com.herman.krang.assertions.assertInvoke
import com.herman.krang.assertions.compile
import com.herman.krang.providers.FunctionArgumentsProvider
import com.tschuchort.compiletesting.KotlinCompilation
import fixtures.classWithFunctionWithoutInterceptAnnotation
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ArgumentsSource
import java.nio.file.Files
import java.nio.file.Path

class GodModeTest {

    private val krangComponentRegistrars = listOf(
        KrangComponentRegistrar(enabledByDefault = true, godModeByDefault = true)
    )

    private val compiler = KotlinCompilation().apply {
        useIR = true
        messageOutputStream = System.out
        compilerPlugins = krangComponentRegistrars
        inheritClassPath = true
        verbose = false
        friendPaths
        workingDir = Files.createTempDirectory(Path.of("build"), "Kotlin-Compilation").toFile()
    }

    @ParameterizedTest
    @ArgumentsSource(FunctionArgumentsProvider::class)
    fun `compilation preserves original body`(arguments: List<Any?>) =
        classWithFunctionWithoutInterceptAnnotation(arguments).compile(compiler) {
            assertCompilation()

            val clazz = classLoader.loadClass("Main")
            val func = clazz.methods.single { it.name == "foo" }

            assertInvoke(listOf("Main.foo"), arguments) {
                func.invoke(clazz.getDeclaredConstructor().newInstance(), *arguments.toTypedArray())
            }
        }
}
