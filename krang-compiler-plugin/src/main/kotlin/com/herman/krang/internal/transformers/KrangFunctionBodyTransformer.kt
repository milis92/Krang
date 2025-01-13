package com.herman.krang.internal.transformers

import com.herman.krang.internal.KrangRuntimeReferences
import com.herman.krang.internal.functionWithName
import com.herman.krang.internal.symbol
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.jvm.ir.isInlineParameter
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.util.*

private val IrPluginContext.anyNullableType
    get() = this.irBuiltIns.anyNType

/**
 * Transforms the function body by adding a call to the Krang runtime
 *
 * @param context Compiler Context
 * @param traceAnnotation Annotation that is used to trace the function call, null if function was instrumented without
 * explicit trace annotation
 * @param parametersFilter Value parameters matching the filter condition will not be passed to krang
 *
 * @return new block body of a function with original statements and a call to the Krang runtime library with
 * arguments as vararg
 */
fun IrFunction.toKrangFunction(
    context: IrPluginContext,
    traceAnnotation: IrConstructorCall?,
    parametersFilter: (IrValueParameter) -> Boolean
): IrBlockBody = DeclarationIrBuilder(context, symbol).irBlockBody {
    val body = body ?: return@irBlockBody

    // Insert krang probe
    +irCall(
        KrangRuntimeReferences.RUNTIME.symbol(context).functionWithName(KrangRuntimeReferences.NOTIFY_LISTENERS)
    ).apply {
        // Add function name
        putValueArgument(
            index = 0,
            valueArgument = irString(kotlinFqName.asString())
        )

        // Add tracing context
        putValueArgument(
            index = 1,
            valueArgument = irCallConstructor(
                callee = KrangRuntimeReferences.TRACING_CONTEXT.symbol(context).constructors.first(),
                typeArguments = emptyList(),
            ).apply {
                putValueArgument(
                    index = 0,
                    valueArgument = traceAnnotation?.let {
                        irCall(traceAnnotation.symbol)
                    } ?: irNull()
                )
            }
        )

        // Add function parameters
        putValueArgument(
            index = 2,
            valueArgument = irVararg(
                elementType = context.anyNullableType,
                values = (listOfNotNull(extensionReceiverParameter) + valueParameters)
                    .filterNot(parametersFilter) // Filter redacted parameters
                    .filterNot {
                        this@toKrangFunction.isInline && it.isInlineParameter()
                    }
                    .map { valueParameter ->
                        irGet(valueParameter)
                    }
            )
        )

        dispatchReceiver = irGetObject(KrangRuntimeReferences.RUNTIME.symbol(context))
    }
    for (statement in body.statements) +statement
}
