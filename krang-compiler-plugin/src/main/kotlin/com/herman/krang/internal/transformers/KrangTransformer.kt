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

import com.herman.krang.internal.krangRuntime
import com.herman.krang.internal.krangTraceAnnotation
import com.herman.krang.internal.krangTraceFunctionEnter
import com.herman.krang.internal.krangTraceFunctionExit
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irCatch
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irConcat
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irTemporary
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.impl.IrTryImpl
import org.jetbrains.kotlin.ir.interpreter.getLastOverridden
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isFakeOverriddenFromAny
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class KrangTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    private val typeUnit = pluginContext.irBuiltIns.unitType
    private val typeThrowable = pluginContext.irBuiltIns.throwableType

    private val classMonotonic =
        pluginContext.referenceClass(FqName("kotlin.time.TimeSource.Monotonic"))!!

    private val funMarkNow =
        pluginContext.referenceFunctions(FqName("kotlin.time.TimeSource.markNow"))
            .single()

    private val funElapsedNow =
        pluginContext.referenceFunctions(FqName("kotlin.time.TimeMark.elapsedNow"))
            .single()

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (shouldVisit(declaration)) {
            declaration.body = traceBodyBuilder(declaration, declaration.body!!)
        }
        return super.visitFunctionNew(declaration)
    }

    private fun shouldVisit(function: IrFunction): Boolean =
        function.body != null && hasAnnotation(function, pluginContext.krangTraceAnnotation)

    private fun hasAnnotation(function: IrFunction, annotationClass: IrClassSymbol): Boolean {
        return if (!function.isFakeOverriddenFromAny()) {
            return when {
                function.hasAnnotation(annotationClass) -> true
                function == function.getLastOverridden() -> false
                else -> hasAnnotation(function.getLastOverridden(), annotationClass)
            }
        } else false
    }

    private fun traceBodyBuilder(
        function: IrFunction,
        body: IrBody
    ): IrBlockBody {
        return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            +traceFunctionEnter(function)

            val startTime = irTemporary(irCall(funMarkNow).also { call ->
                call.dispatchReceiver = irGetObject(classMonotonic)
            })

            val tryBlock = irBlock(resultType = function.returnType) {
                for (statement in body.statements) +statement
                if (function.returnType == typeUnit) +traceFunctionExit(function, startTime)
            }.transform(KrangReturnTransformer(function, startTime), null)

            val throwable = buildVariable(
                scope.getLocalDeclarationParent(), startOffset, endOffset, IrDeclarationOrigin.CATCH_PARAMETER,
                Name.identifier("t"), typeThrowable
            )

            +IrTryImpl(startOffset, endOffset, tryBlock.type).also { irTry ->
                irTry.tryResult = tryBlock
                irTry.catches += irCatch(throwable, irBlock {
                    +traceFunctionExit(function, startTime, irGet(throwable))
                    +irThrow(irGet(throwable))
                })
            }
        }
    }

    private fun IrBuilderWithScope.traceFunctionEnter(
        function: IrFunction
    ): IrCall {
        val concat = irConcat()
        concat.addArgument(irString("⇢ ${function.name}("))
        for ((index, valueParameter) in function.valueParameters.withIndex()) {
            if (index > 0) concat.addArgument(irString(", "))
            concat.addArgument(irString("${valueParameter.name}="))
            concat.addArgument(irGet(valueParameter))
        }
        concat.addArgument(irString(")"))
        return irCall(
            pluginContext.krangTraceFunctionEnter
        ).apply {
            putValueArgument(0, concat)
            dispatchReceiver = irGetObject(pluginContext.krangRuntime)
        }
    }

    private fun IrBuilderWithScope.traceFunctionExit(
        function: IrFunction,
        startTime: IrValueDeclaration,
        result: IrExpression? = null
    ): IrCall {
        val concat = irConcat()
        concat.addArgument(irString("⇠ ${function.name} ["))
        concat.addArgument(irCall(funElapsedNow).also { call ->
            call.dispatchReceiver = irGet(startTime)
        })
        if (result != null) {
            concat.addArgument(irString("] = "))
            concat.addArgument(result)
        } else {
            concat.addArgument(irString("]"))
        }

        return irCall(
            pluginContext.krangTraceFunctionExit
        ).apply {
            putValueArgument(0, concat)
            dispatchReceiver = irGetObject(pluginContext.krangRuntime)
        }
    }

    inner class KrangReturnTransformer(
        private val function: IrFunction,
        private val startTime: IrVariable
    ) : IrElementTransformerVoidWithContext() {
        override fun visitReturn(expression: IrReturn): IrExpression {
            if (expression.returnTargetSymbol != function.symbol) return super.visitReturn(expression)

            return DeclarationIrBuilder(pluginContext, function.symbol).irBlock {
                val result = irTemporary(expression.value)
                +traceFunctionExit(function, startTime, irGet(result))
                +expression.apply {
                    value = irGet(result)
                }
            }
        }
    }
}
