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

package com.herman.krang.internal.transformers

import com.herman.krang.internal.i
import com.herman.krang.runtime.Krang
import com.herman.krang.runtime.annotations.Trace
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.name.FqName

class KrangTransformer(
    private val context: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    private val annotationClass =
        context.referenceClass(FqName(Trace::class.qualifiedName!!)) ?: throw ClassNotFoundException()

    private val krangRuntime =
        context.referenceClass(FqName(Krang::class.qualifiedName!!)) ?: throw ClassNotFoundException()

    private val krangTrance =
        krangRuntime.getSimpleFunction("trace")!!

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (shouldVisit(declaration)) {
            declaration.body = traceBodyBuilder(declaration)
            messageCollector.i("Transforming: ${declaration.name}")
        }
        return super.visitFunctionNew(declaration)
    }

    private fun shouldVisit(function: IrFunction): Boolean {
        return function.body != null && function.hasAnnotation(annotationClass)
    }

    private fun traceBodyBuilder(
        function: IrFunction
    ): IrBlockBody {
        return DeclarationIrBuilder(context, function.symbol).irBlockBody {
            +irCall(krangTrance).also { call ->
                call.putValueArgument(0, irString(function.name.asString()))
                for ((index, valueParameter) in function.valueParameters.withIndex()) {
                    call.putValueArgument(index.inc(), irGet(valueParameter))
                }
                call.dispatchReceiver = irGetObject(krangRuntime)
            }
        }
    }
}
