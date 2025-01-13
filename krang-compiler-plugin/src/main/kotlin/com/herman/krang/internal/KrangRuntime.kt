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

import com.herman.krang.runtime.FunctionCallListener
import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.TracingContext
import com.herman.krang.runtime.annotations.Redact
import com.herman.krang.runtime.annotations.Trace
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.name.*

internal object KrangRuntimeReferences {
    val TRACE_ANNOTATION: FqName =
        FqName(Trace::class.qualifiedName!!)

    val REDACT_ANNOTATION: FqName =
        FqName(Redact::class.qualifiedName!!)

    val RUNTIME: FqName =
        FqName(Krang::class.qualifiedName!!)

    val FUNCTION_CALL_LISTENER: FqName =
        FqName(FunctionCallListener::class.qualifiedName!!)

    val TRACING_CONTEXT: FqName =
        FqName(TracingContext::class.qualifiedName!!)

    val NOTIFY_LISTENERS: String = "notifyListeners"
}

internal val FqName.classId: ClassId
    get() = ClassId.topLevel(this)

fun FqName.symbol(context: IrPluginContext): IrClassSymbol =
    context.referenceClass(this.classId) ?: throw ClassNotFoundException("Class not found with $this")

fun IrClassSymbol.functionWithName(name: String): IrFunctionSymbol =
    this.functionByName(name)
