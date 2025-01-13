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

/**
 * Use `@Redact` to mark function value parameters that should not be passed to `Krang` during runtime.

 * By default, all parameters are passed to `Krang`, __unless explicitly marked with this annotation__.
 *
 * This allows developers to control and limit the information that flows through `Krang`,
 * which can be particularly useful when dealing with large data or sensitive data objects.
 *
 * #### Usage:
 * `@Redact` annotation can be applied to either a value parameter directly or a class type
 * definition used as a value parameter.
 *
 * ##### Explicitly marking value parameters with `@Redact` annotation:
 *
 * ```
 * class MyClass {
 *     @Trace
 *     fun myFunction(@Redact param: Any) {
 *         // Implementation
 *     }
 * }
 *
 * fun main() {
 *     Krang.addListener { name, arguments ->
 *         println("Function with $name and $arguments called")
 *     }
 *
 *     MyClass().myFunction(Any())
 * }
 * ```
 *
 * In this example, when  myFunction gets called, parameter `param` is not going to be passed to `Krang`,
 * and `arguments` in the listener is going to be an empty array
 *
 * #### Marking class type with `@Redact` annotation:
 *
 * __NOTE: Properties of the annotated class are still going to be passed to `Krang` if used directly.__
 *
 * ```
 * @Redact
 * data class DataThatShouldBeRedacted(val data: String)
 *
 * class MyClass {
 *    @Trace
 *    fun myFunction(param: DataThatShouldBeRedacted) {
 *    // Implementation
 *    }
 * }
 *
 * fun main() {
 *     Krang.addListener { name, arguments ->
 *         println("Function with $name and $arguments called")
 *     }
 *
 *     MyClass().myFunction(DataThatShouldBeRedacted("sensitive data"))
 * }
 * ```
 *
 * In this example, when `myFunction` gets called, parameter `param` is not going to be passed to `Krang`,
 * and `arguments` in the listener is going to be an empty array
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.SOURCE)
annotation class Redact
