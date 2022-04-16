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

import com.herman.krang.internal.krangInterceptFunctionCall
import com.herman.krang.internal.krangRedactAnnotation
import com.herman.krang.internal.krangRuntime
import com.herman.krang.internal.krangTraceAnnotation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.deepCopyWithVariables
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.interpreter.getLastOverridden
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isFakeOverriddenFromAny
import org.jetbrains.kotlin.ir.util.statements

class KrangTransformer(
    private val pluginContext: IrPluginContext,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    private val anyNullableType = pluginContext.irBuiltIns.anyNType

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (shouldVisit(declaration)) {
            declaration.body = probedBodyBuilder(declaration)
        }
        return super.visitFunctionNew(declaration)
    }

    /**
     * Determines if function should be transformed by Krang
     *
     * @return True if function or its parent satisfies precondition for transformations
     */
    private fun shouldVisit(function: IrFunction): Boolean =
        function.body != null && hasAnnotation(function, pluginContext.krangTraceAnnotation)

    /**
     * Recursively checks if provided function or its parent has annotation applied to its signature
     *
     * @return True if function or its parent has annotation applied
     */
    private fun hasAnnotation(function: IrFunction, annotationClass: IrClassSymbol): Boolean {
        return if (!function.isFakeOverriddenFromAny()) {
            return when {
                function.hasAnnotation(annotationClass) -> true
                function == function.getLastOverridden() -> false
                else -> hasAnnotation(function.getLastOverridden(), annotationClass)
            }
        } else false
    }

    /**
     * Transforms the function body
     *
     * @return new block body of a function with original statements and [irCall] to the runtime library with arguments
     * as vararg
     */
    private fun probedBodyBuilder(
        function: IrFunction
    ): IrBlockBody {
        return DeclarationIrBuilder(pluginContext, function.symbol).irBlockBody {
            //Construct vararg from function parameters
            val argsAsVarArg = varargOf(
                pluginContext,
                anyNullableType,
                function.valueParameters
                    .filter { !it.hasAnnotation(pluginContext.krangRedactAnnotation) }
                    .map { valueParameter ->
                        irGet(valueParameter)
                    }
            )

            //Insert the krang probe
            +irCall(pluginContext.krangInterceptFunctionCall).apply {
                putValueArgument(0, irString("${function.name}"))
                putValueArgument(1, argsAsVarArg.deepCopyWithVariables())
                dispatchReceiver = irGetObject(pluginContext.krangRuntime)
            }

            //Apply original statements
            for (statement in function.body!!.statements) +statement
        }
    }

    private fun varargOf(
        pluginContext: IrPluginContext,
        elementType: IrType,
        elements: Iterable<IrExpression>
    ) = IrVarargImpl(
        UNDEFINED_OFFSET,
        UNDEFINED_OFFSET,
        pluginContext.irBuiltIns.arrayClass.typeWith(elementType),
        elementType,
        elements.toList()
    )
}
