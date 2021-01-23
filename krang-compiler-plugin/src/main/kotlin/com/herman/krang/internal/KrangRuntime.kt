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

package com.herman.krang.internal

import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.annotations.Intercept
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.FqName

/**
 * Reference to the Krang annotation
 */
val IrPluginContext.krangTraceAnnotation
    get() = referenceClass(FqName(Intercept::class.qualifiedName!!)) ?: throw ClassNotFoundException()

/**
 * Reference to the runtime
 */
val IrPluginContext.krangRuntime
    get() = referenceClass(FqName(Krang::class.qualifiedName!!)) ?: throw ClassNotFoundException()

/**
 * Reference to the runtime interceptor function
 */
val IrPluginContext.krangInterceptFunctionCall
    get() = krangRuntime.getSimpleFunction("interceptFunctionCall")!!
