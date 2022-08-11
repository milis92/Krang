package com.herman.krang.internal.transformers

import com.herman.krang.internal.krangInterceptFunctionCall
import com.herman.krang.internal.krangRuntime
import org.jetbrains.kotlin.backend.common.deepCopyWithVariables
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.statements

private val IrPluginContext.anyNullableType
    get() = this.irBuiltIns.anyNType

/**
 * Transforms the function body
 *
 * @return new block body of a function with original statements and [irCall] to the runtime library with arguments
 * as vararg
 */
fun IrFunction.toKrangFunction(
    context: IrPluginContext,
    parametersFilter: (IrValueParameter) -> Boolean
): IrBlockBody = DeclarationIrBuilder(context, symbol).irBlockBody {
    // Construct vararg from function parameters
    val argsAsVarArg = context.varargOf(
        context.anyNullableType,
        valueParameters
            .filter(parametersFilter)
            .map { valueParameter ->
                irGet(valueParameter)
            }
    )
    // Insert krang probe
    +irCall(context.krangInterceptFunctionCall).apply {
        putValueArgument(0, irString("$name"))
        putValueArgument(1, argsAsVarArg.deepCopyWithVariables())
        dispatchReceiver = irGetObject(context.krangRuntime)
    }
    // Apply original statements
    for (statement in body!!.statements) +statement
}

private fun IrPluginContext.varargOf(
    elementType: IrType,
    elements: Iterable<IrExpression>
) = IrVarargImpl(
    UNDEFINED_OFFSET,
    UNDEFINED_OFFSET,
    irBuiltIns.arrayClass.typeWith(elementType),
    elementType,
    elements.toList()
)
