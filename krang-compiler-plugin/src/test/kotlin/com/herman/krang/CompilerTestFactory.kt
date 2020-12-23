/*
 * Copyright (C) 2020 Ivan Milisavljevic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.herman.krang

import com.herman.krang.CompilerTest.NoArgsFunctions
import com.tschuchort.compiletesting.KotlinCompilation
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestInstanceFactory
import org.junit.jupiter.api.extension.TestInstanceFactoryContext
import org.junit.jupiter.api.extension.TestInstantiationException

class CompilerTestFactory : TestInstanceFactory {

    private val krangComponentRegistrars = listOf(
        KrangComponentRegistrar()
    )

    override fun createTestInstance(
        factoryContext: TestInstanceFactoryContext,
        extensionContext: ExtensionContext
    ): Any {
        val compilerTest = CompilerTest(KotlinCompilation().apply {
            useIR = true
            messageOutputStream = System.out
            compilerPlugins = krangComponentRegistrars
            inheritClassPath = true
            verbose = true
        })

        return when (factoryContext.testClass) {
            CompilerTest::class.java -> compilerTest
            NoArgsFunctions::class.java -> compilerTest.NoArgsFunctions()
            else -> throw TestInstantiationException(
                "There is no factory context registered for ${factoryContext.testClass}"
            )
        }
    }
}