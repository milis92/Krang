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
 * This annotation is used to mark function parameters that shouldn't be passed to Krang during runtime.

 * By default, all parameters are passed to krang, unless explicitly marked with this annotation.
 *
 * This mechanism allows developers to control and limit the information that flows
 * through Krang's, which can be particularly useful when dealing
 * with large data objects or sensitive data.
 *
 * Example usage:
 *
 * ```
 * class MyClass {
 *
 *     @Intercept
 *     fun myFunction(@Redact param: Any) {
 *         // Implementation
 *     }
 * }
 *
 * fun main() {
 *     Krang.addListener { name, arguments ->
 *         println("Function with $name and ${arguments.joinToString()} called")
 *     }
 *
 *     MyClass().myFunction(Any())
 * }
 *
 *
 * ```
 *
 * In this example, when  myFunction gets called, parameter `param` is not going to be passed to krang,
 * and arguments is going to be an empty array
 */
@Retention(AnnotationRetention.SOURCE)
annotation class Redact
