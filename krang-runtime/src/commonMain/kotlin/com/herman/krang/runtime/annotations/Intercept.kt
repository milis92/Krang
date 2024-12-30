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

package com.herman.krang.runtime.annotations

import com.herman.krang.runtime.FunctionCallListener
import com.herman.krang.runtime.TracingContext

/**
 * Classes or functions marked with this annotation will be instrumented by krang compiler plugin
 *
 * By default, any function __without__ this annotation will be ignored by Krang, unless god mode is enabled.
 * If god mode is enabled, all valid kotlin functions inside a codebase will be instrumented,
 * regardless if they have this annotation or not.
 *
 * This annotation is applicable to the class or a specific function.
 * When applied to a class, all contained functions will be instrumented, and will inform [FunctionCallListener]
 * once called. When applied to a function, only that specific function is going to notify the listener.
 *
 * _Note on inheritance:_ Krang fully respects inheritance, in as sense that if the parent class or a function is
 * annotated, child function will be instrumented if the parent has the [Intercept] annotation applied.
 *
 * Usage examples:
 *
 * Class usage example:
 *
 * ```
 * @Intercept
 * class Foo {
 *     fun bar(param1: String) {
 *       // DO SOMETHING
 *     }
 *
 *     fun bar1(param2: String){
 *       // DO SOMETHING
 *     }
 * }
 *
 * fun main() {
 *     Krang.addListener { name, arguments ->
 *         println("Function with $name and ${arguments.joinToString()} called")
 *     }
 *
 *     Foo().bar("bazz") // Prints "Function with bar and bazz called
 *     Foo().bar1("bazz2") // Prints "Function with bar1 and bazz2 called
 * }
 * ```
 *
 * Function usage example:
 *
 * ```
 * class Foo {
 *     @Intercept
 *     fun bar(param1: String) {
 *       // DO SOMETHING
 *     }
 * }
 *
 * fun main() {
 *     Krang.addListener { name, arguments ->
 *         println("Function with $name and ${arguments.joinToString()} called")
 *     }
 *
 *     Foo().bar("bazz") // Prints "Function with bar and bazz called
 * }
 * ```
 **/
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.CONSTRUCTOR,
)
@Retention(AnnotationRetention.SOURCE)
@Deprecated(
    message = "This annotation is deprecated and will be removed in the next major release. Use @Trace annotation",
    replaceWith = ReplaceWith(
        expression = "Trace",
        "com.herman.krang.runtime.annotations.Trace"
    ),
    level = DeprecationLevel.ERROR
)
annotation class Intercept

annotation class Trace