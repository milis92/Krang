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

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName

val krangInterceptAnnotation: FqName
    get() = FqName("com.herman.krang.runtime.annotations.Intercept")

val krangRedactAnnotation: FqName
    get() = FqName("com.herman.krang.runtime.annotations.Redact")

val krangRuntime: FqName
    get() = FqName("com.herman.krang.runtime.Krang")

val krangRuntimeClassId: ClassId
    get() = ClassId.topLevel(krangRuntime)

val IrPluginContext.krangRuntimeClassSymbol: IrClassSymbol
    get() =  referenceClass(krangRuntimeClassId) ?: throw ClassNotFoundException()

val krangFunctioNCallListener: FqName
    get() = FqName("com.herman.krang.runtime.FunctionCallListener")

val krangFunctionCallListenerClassId: ClassId
    get() = ClassId.topLevel(krangFunctioNCallListener)

val IrPluginContext.krangNotifyListeners: IrFunctionSymbol
    get() = krangRuntimeClassSymbol.functionByName("notifyListeners") ?: throw NoSuchMethodException()
