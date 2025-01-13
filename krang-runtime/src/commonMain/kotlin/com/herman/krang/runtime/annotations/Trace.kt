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


/**
 * Classes or functions marked with this annotation will be instrumented by krang compiler plugin
 *
 * By default, any function __without__ this annotation will be ignored by Krang, unless god mode is enabled.
 * If god mode is enabled, anything that can be represented as a valid kotlin functions inside a codebase will be instrumented,
 * regardless if they have this annotation or not.
 *
 * This annotation is applicable to the multiple targets:
 * - Class - all functions inside the class will be instrumented
 * - Function - only annotated function is going to be instrumented
 * - Property getter/setter - property access will be instrumented
 * - File - all functions and getters/setters inside the file will be instrumented
 * - Constructor - constructor will be instrumented
 *
 * #### Usage:
 *
 * Class usage example:
 * ```
 * @Trace
 * class Foo {
 *    fun bar(param1: String) {
 *    // DO SOMETHING
 *    }
 * }
 *
 * fun main() {
 *    Krang.addListener { name, arguments ->
 *      println("Function with $name and ${arguments.joinToString()} called")
 *    }
 *
 *    Foo().bar("bazz") // Prints "Function with bar and bazz called
 *  }
 * ```
 * Function usage example:
 * ```
 * class Foo {
 *   @Trace
 *   fun bar(param1: String) {
 *   // DO SOMETHING
 *   }
 * }
 *
 * fun main() {
 *  Krang.addListener { name, arguments ->
 *      println("Function with $name and ${arguments.joinToString()} called")
 *  }
 *
 *  Foo().bar("bazz") // Prints "Function with bar and bazz called
 * }
 * ```
 *
 * File usage example:
 * ```
 * @file:Trace
 *
 * fun bar(param1: String) {
 *  // DO SOMETHING
 * }
 *
 * fun main() {
 *   Krang.addListener { name, arguments ->
 *     println("Function with $name and ${arguments.joinToString()} called")
 *   }
 *
 *    bar("bazz") // Prints "Function with bar and bazz called
 *  }
 * ```
 *
 * Getter and setter usage example:
 * ```
 * class Foo {
 *  var bar: String
 *  @Trace get() {
 *      // DO SOMETHING
 *  }
 *  @Trace set(value) {
 *    // DO SOMETHING
 *    field = value
 *  }
 *
 * fun main() {
 *   Krang.addListener { name, arguments ->
 *     println("Function with $name and ${arguments.joinToString()} called")
 *   }
 *
 *    Foo().bar = "bazz" // Prints "Function with <set-bar> and bazz called
 *    val value = Foo().bar // Prints "Function with <get-bar> and called
 * }
 *  ```
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.FILE,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
)
@Retention(AnnotationRetention.SOURCE)
annotation class Trace