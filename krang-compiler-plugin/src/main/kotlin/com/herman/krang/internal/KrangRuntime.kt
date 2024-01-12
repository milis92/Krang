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
import com.herman.krang.runtime.annotations.Redact
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

// Reference to the Krang Intercept annotation
val IrPluginContext.krangInterceptAnnotation: IrClassSymbol
    get() = referenceClass(ClassId.topLevel(FqName(Intercept::class.simpleName!!))) ?: throw ClassNotFoundException()

// Reference to the Krang Redact annotation
val IrPluginContext.krangRedactAnnotation: IrClassSymbol
    get() = referenceClass(ClassId.topLevel(FqName(Redact::class.qualifiedName!!))) ?: throw ClassNotFoundException()

// Reference to the Krang Runtime
val IrPluginContext.krangRuntime: IrClassSymbol
    get() = referenceClass(ClassId.topLevel(FqName(Krang::class.qualifiedName!!))) ?: throw ClassNotFoundException()

// Reference to the runtime interceptor function
val IrPluginContext.krangInterceptFunctionCall: IrFunctionSymbol
    get() = krangRuntime.getSimpleFunction("notifyListeners")!!
